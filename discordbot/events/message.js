const addItem = require('../commands/additem');
const campaign = require('../commands/campaign');
const character = require('../commands/character');

const alias = {
    'campaign': campaign,
    'cid': campaign,
    'character': character,
    'pid': character,
    'char': character,
    'additem': addItem,
};

module.exports = (client, message) => {
    if (message.author === client.user) {
        return;
    }

    if (message.content.startsWith("$")) {
        const command = message.content.split(" ")[0].slice(1).toString();
        const matchedCommand = Object.keys(alias).filter(key => key === command);
        if (matchedCommand.length < 1) {
            message.channel.send("No command matched. Type $help for a list of commands");
        } else {
            alias[matchedCommand[0]](message);
        }
    }
}