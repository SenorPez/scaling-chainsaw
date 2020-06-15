require("dotenv").config();
const regex = /^.+?\s+(\d+(?=\s))?\s*(.+?)(?=\s+(--.+)|\s*$)/g;
const argregex = /--(\S)\s+(.+?)(?=\s+--|\s*$)/g;
const fetch = require("node-fetch");

const api = require('../service/api')
const state = require('../service/state');

const getToken = require('../service/authtoken');

const args = {
    'r': null
}

function CampaignNotSetError() {
    this.message = 'Campaign not set; use $campaign to set';
}

function CharacterNotSetError() {
    this.message = 'Character not set; use $character to set';
}

function ParseError() {
    this.message = "Usage: $additem <item name> [--r <remark>]";
}

function ItemNotFoundError(itemName) {
    this.message = `Item containing '${itemName}' not found`;
}

function MultipleMatchError(itemName, data) {
    this.message = `Multiple items containing '${itemName}' found:`;
    data.forEach(item => this.message = this.message + `\nID: ${item.id} Name: ${item.name}`);
}

function ItemIdNotFoundError(itemId) {
    this.message = `Item with ID of ${itemId} not found`;
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
                message.channel.send('Usage: $additem &lt;quantity&gt; &lt;item name&gt; [--r &ltremark&gt]');
                return;
            }
        }
    } else {
        message.channel.send('Usage: $additem &lt;quantity&gt; &lt;item name&gt; [--r &ltremark&gt]');
        return;
    }

    if (matches[0]) {
        const quantity = matches[0][1] ? matches[0][1] : 1;
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
                    postTransaction(message, values[0][0], quantity, values[1].access_token, args);
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

function postTransaction(message, item, quantity, accessToken, args) {
    const newTransaction = {
        item: item.id,
        quantity: quantity,
        remark: args.r
    }
    console.log(newTransaction);
    const authHeader = `bearer ${accessToken}`;

    fetch(`https://www.loot.senorpez.com/campaigns/${state.getCampaignId()}/characters/${state.getCharacterId()}/itemtransactions`, {
        method: "post",
        body: JSON.stringify(newTransaction),
        headers: {
            "Content-Type": "application/hal+json",
            "Authorization": authHeader,
        }
    })
        .then(response => response.json())
        .then(data => {
            message.channel.send(`Added ${quantity} ${item.name} to ${data.name}`);
            updatedItem = data.inventory.filter(invItem => invItem.name === item.name);
            message.channel.send(`Now has ${updatedItem[0].quantity} ${updatedItem[0].name}`);
            console.log(data);
        });
}

module.exports.parseMessage = (message) => {
    return new Promise(resolve => {
        if (state.getCampaignId() === null) {
            throw new CampaignNotSetError();
        }

        if (state.getCharacterId() === null) {
            throw new CharacterNotSetError();
        }

        const matches = [...message.content.matchAll(regex)];

        if (matches[0]) {
            resolve(matches[0]);
        }
        throw new ParseError();
    })
}

module.exports.parseCommand = (matches) => {
    return new Promise((resolve, reject) => {
        /*
        Resolve: Text search for item
        Reject: Lookup item by id
         */
        const quantity = matches[1] ? matches[1] : 1;
        const arguments = matches[3];

        const itemId = Number(matches[2]).valueOf();
        isNaN(itemId) ? resolve({
            id: matches[2],
            quantity: quantity,
            args: arguments
        }) : reject({
            id: itemId,
            quantity: quantity,
            args: arguments});
    });
}

module.exports.parseArguments = (argumentString) => {
    return new Promise(resolve => {
        for (const key of Object.keys(args)) {
            args[key] = null;
        }

        if (argumentString === null) {
            resolve(args);
        }

        const matches = [...argumentString.matchAll(argregex)];
        matches.forEach(match => {
            if (match[1] in args) {
                args[match[1]] = match[2];
            }
        });
        resolve(args);
    });
}

module.exports.getItems = () => {
    return api.get(process.env.API_URL)
        .then(response => response.json())
        .then(apiindex => api.get(apiindex._links['loot-api:lootitems'].href));
}

module.exports.findItemByName = (itemName) => {
    return module.exports.getItems()
        .then(response => response.json())
        .then(items => {
            const embeddedItem = items._embedded['loot-api:lootitem'].filter(item => item.name.toLowerCase().includes(itemName.toLowerCase()));
            if (embeddedItem.length === 1) {
                return api.get(embeddedItem.pop()._links.self.href);
            } else if (embeddedItem.length < 1) {
                throw new ItemNotFoundError(itemName);
            }
            throw new MultipleMatchError(itemName, embeddedItem);
        });
}

module.exports.findItemById = (itemId) => {
    return module.exports.getItems()
        .then(response => response.json())
        .then(items => {
            const embeddedItem = items._embedded['loot-api:lootitem'].filter(embeddedItem => embeddedItem.id === itemId);
            if (embeddedItem.length === 1) {
                return api.get(embeddedItem.pop()._links.self.href);
            }
            throw new ItemIdNotFoundError(itemId);
        });
}