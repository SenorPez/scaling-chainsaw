const getToken = require('../service/authtoken');
const {parseMessage, parseArguments, parseCommand, findItemByName, findItemById, postTransaction} = require('../commands/additem');

module.exports = (message) => {
    const tokenPromise = getToken();
    const itemPromise = parseMessage(message)
        .then(matches => parseArguments(matches))
        .then(
            searchParam => findItemByName(searchParam),
            searchParam => findItemById(searchParam)
        );
    const argsPromise = parseMessage(message)
        .then(matches => parseCommand(matches));

    Promise.all([tokenPromise, itemPromise, argsPromise])
        .then(values => {
            const item = values[1];
            const arguments = values[2];
            arguments.quantity = -arguments.quantity;
            const token = values[0];
            postTransaction(message, item, arguments, token);
        });
};