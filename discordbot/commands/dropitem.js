const getToken = require('../service/authtoken');
const {parseMessage, parseArguments, parseCommand, findItemByName, findItemById, postTransaction} = require('../commands/additem');

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
            return postTransaction(message, item, arguments, token);
        })
        .catch(error => error);
};
