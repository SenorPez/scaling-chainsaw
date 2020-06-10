const regex = /^.+?\s+(\d+)\s+(.+)/g;
const fetch = require("node-fetch");
const state = require('../service/state');

const getToken = require('../service/authtoken');

module.exports = (message) => {
    if (state.getCampaignId() === null) {
        message.channel.send("Campaign must be set; use the $campaign command");
        return
    }

    if (state.getCharacterId() === null) {
        message.channel.send("Character must be set; use the $character command");
        return
    }

    const matches = [...message.content.matchAll(regex)];
    if (matches[0]) {
        const quantity = matches[0][1];
        const itemname = matches[0][2];

        const itemPromise = getItemId(itemname);
        const tokenPromise = getToken();

        Promise.all([itemPromise, tokenPromise])
            .then(values => {
                if (values[0] !== undefined) {
                    postTransaction(message, values[0][0], quantity, values[1].access_token);
                }
            })
    }
}

function getItems() {
    return fetch("https://www.loot.senorpez.com/items")
        .then(response => response.json());
}

function getItemId(itemname) {
    return getItems()
        .then(data => data._embedded['loot-api:lootitem'].filter(item => item.name === itemname));
}

function postTransaction(message, item, quantity, accessToken) {
    const newTransaction = {
        item: item.id,
        quantity: quantity
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