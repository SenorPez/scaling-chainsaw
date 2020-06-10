const campaign = require('../commands/campaign');

const alias = {
    'campaign': campaign,
    'cid': campaign,
};

module.exports = (client, state, message) => {
    if (message.author === client.user) {
        return;
    }

    console.log("Message S", state);
    if (message.content.startsWith("$")) {
        const command = message.content.split(" ")[0].slice(1).toString();
        const matchedCommand = Object.keys(alias).filter(key => key === command);
        if (matchedCommand.length < 1) {
            message.channel.send("No command matched. Type $help for a list of commands");
        } else {
            console.log(alias[matchedCommand[0]](message, state));
        }
    }
    console.log("Message", state);
}