const regex = /^.+?\s+(\d+)\s+(.+?)(?=\s+(--.+)|\s*$)/g;
const argregex = /--(\S)\s+(.+?)(?=\s+--|\s*$)/g;
const fetch = require("node-fetch");
const state = require('../service/state');

const getToken = require('../service/authtoken');

const args = {
    'r': null
}

module.exports = (message) => {
    if (state.getCampaignId() === null) {
        message.channel.send("Campaign must be set; use the $campaign command");
        return
    }

    if (state.getCharacterId() === null) {
        message.channel.send("Character must be set; use the $character command");
        return;
    }

    const matches = [...message.content.matchAll(regex)];

    if (matches[0]) {
        if (matches[0][3]) {
            const argmatches = [...matches[0][3].matchAll(argregex)];
            if (argmatches[0]) {
                argmatches.forEach(match => {
                    if (match[1] in args) {
                        args[match[1]] = match[2];
                    }
                })
            } else {
                message.channel.send('Usage: $setcharges [&lt;quantity&gt;] &lt;item name&gt; [--r &ltremark&gt]');
                return;
            }
        }
    } else {
        message.channel.send('Usage: $additem [&lt;quantity&gt;] &lt;item name&gt; [--r &ltremark&gt]');
        return;
    }

    if (matches[0]) {
        const charges = matches[0][1];
        const itemname = matches[0][2];

        const itemPromise = getItemId(itemname);
        const tokenPromise = getToken();

        Promise.all([itemPromise, tokenPromise])
            .then(values => {
                if (values[0].length < 1) {
                    message.channel.send(`Item ${itemname} not found`);
                } else if (values[0].length > 1) {
                    message.channel.send(`Multiple items found:`);
                    values[0].forEach(item => message.channel.send(`${item.name}`));
                } else {
                    postTransaction(message, values[0][0], charges, values[1].access_token, args);
                }
            })
            .then(() => args.r = null);
    }
}

function getItems() {
    return fetch("https://www.loot.senorpez.com/items")
        .then(response => response.json());
}

function getItemId(itemname) {
    return getItems()
        .then(data => data._embedded['loot-api:lootitem'].filter(item => item.name.toLowerCase().includes(itemname.toLowerCase())));
}

function postTransaction(message, item, charges, accessToken, args) {
    const newTransaction = {
        item: item.id,
        quantity: 0,
        remark: args.r ? args.r + ` (Charges ${charges})` : `Charges ${charges}`
    };
    const setCharges = {
        charges: charges
    };
    const authHeader = `bearer ${accessToken}`;

    const transactionPromise = fetch(`https://www.loot.senorpez.com/campaigns/${state.getCampaignId()}/characters/${state.getCharacterId()}/itemtransactions`, {
        method: "post",
        body: JSON.stringify(newTransaction),
        headers: {
            "Content-Type": "application/hal+json",
            "Authorization": authHeader,
        }
    });
    const putPromise = fetch(`https://www.loot.senorpez.com/items/${item.id}`, {
        method: "put",
        body: JSON.stringify(setCharges),
        headers: {
            'Content-Type': 'application/hal+json',
            'Authorization': authHeader
        }
    });

    Promise.all([transactionPromise, putPromise])
        .then(responses => Promise.all(responses.map(r => r.json())))
        .then(data => {
            updatedItem = data[1];
            message.channel.send(`Set ${updatedItem.name} to ${updatedItem.charges} charges`);
        });
}