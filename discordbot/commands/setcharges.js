const getToken = require('../service/authtoken');
const {parseMessage, parseArguments, parseCommand, findItemByName, findItemById} = require('../commands/additem');
const fetch = require("node-fetch");
const state = require('../service/state');

module.exports = (message) => {
    return Promise.resolve();
    // const tokenPromise = getToken();
    // const itemPromise = parseMessage(message)
    //     .then(matches => parseCommand(matches))
    //     .then(searchParam => {
    //         return (typeof searchParam === 'number')
    //             ? findItemById(searchParam)
    //             : findItemByName(searchParam);
    //     })
    //     .then(result => result.json())
    //     .catch(error => {
    //         message.channel.send(error.message);
    //         throw error;
    //     });
    // const argsPromise = parseMessage(message)
    //     .then(matches => parseArguments(matches))
    //     .catch(error => {
    //         message.channel.send(error.message);
    //         throw error;
    //     });
    //
    // return Promise.all([tokenPromise, itemPromise, argsPromise])
    //     .then(values => {
    //         const item = values[1];
    //         const arguments = values[2];
    //         const token = values[0];
    //         return module.exports.postCharges(message, item, arguments, token);
    //     })
    //     .catch(error => error);
};

module.exports.postCharges = (message, item, arguments, token) => {
    const remark = arguments.arguments.r === null
        ? `Set charges to ${arguments.quantity}`
        : arguments.arguments.r + ` (Set charges to ${arguments.quantity}`;
    const newTransaction = {
        item: item.id,
        remark: remark
    }
    const setCharges = {
        charges: arguments.quantity
    }
    const authHeader = `bearer ${token.access_token}`;

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

    return Promise.all([transactionPromise, putPromise])
        .then(responses => Promise.all(responses.map(r => r.json())))
        .then(data => {
            updatedItem = data[1];
            message.channel.send(`Set ${updatedItem.name} to ${updatedItem.charges} charges`);
        });
}