const getToken = require('../service/authtoken');
const state = require('../service/state');

const {parseMessage, parseArguments, parseCommand, findItemByName, findItemById, postTransaction} = require('../commands/additem');
const {findCharacterById} = require('../commands/character');

function ParseError() {
    this.message = "Usage: $dropitem <item name> [--r <remark>]";
}

module.exports = (message) => {
    const tokenPromise = getToken();
    const itemPromise = parseMessage(message, new ParseError())
        .then(matches => parseCommand(matches))
        .then(searchParam => {
            return (typeof searchParam === 'number')
                ? findItemById(searchParam)
                : findItemByName(searchParam);
        })
        .then(result => result.json())
        .catch(error => {
            message.channel.send(error.message);
            throw error;
        });
    const argsPromise = parseMessage(message, new ParseError())
        .then(matches => parseArguments(matches))
        .catch(error => {
            message.channel.send(error.message);
            throw error;
        });

    return Promise.all([tokenPromise, itemPromise, argsPromise])
        .then(values => {
            const item = values[1];
            const arguments = values[2];
            arguments.quantity = -arguments.quantity;
            const token = values[0];

            return module.exports.findItemOnCharacter(item, -arguments.quantity)
                .then(() => postTransaction(message, item, arguments, token))
                .catch(error => {
                    message.channel.send(error.message);
                    throw error
                });
        })
        .catch(error => error);
};

function DropItemError(character, item, hasQuantity, dropQuantity) {
    this.message = `Cannot drop ${dropQuantity} ${item.name}; ${character.name} has ${hasQuantity} ${item.name}`;
}

module.exports.findItemOnCharacter = (item, quantity) => {
    return findCharacterById(state.getCharacterId())
        .then(response => response.json())
        .then(character => {
            const filteredItem = character.inventory.filter(inventoryItem => inventoryItem.id === item.id);
            if (filteredItem.length === 1) {
                const hasQuantity = filteredItem.pop().quantity;
                if (hasQuantity >= quantity) {
                    return item;
                }
                throw new DropItemError(character, item, hasQuantity, quantity);
            }
            throw new DropItemError(character, item, 0, quantity);
        });
}