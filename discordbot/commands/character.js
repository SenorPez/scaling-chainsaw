const regex = /^.+?(?:\s)+(.+)/g;
const fetch = require("node-fetch");
const state = require('../service/state');

module.exports = (message) => {
    if (state.getCampaignId() === null) {
        message.channel.send("Campaign must be set; use the $campaign command");
        return
    }

    const matches = [...message.content.matchAll(regex)];
    if (matches[0]) {
        const characterId = Number(matches[0][1]).valueOf();
        if (isNaN(characterId)) {
            findCharacterByName(matches[0][1])
                .then(data => {
                    if (data.length < 1) {
                        message.channel.send(`${matches[0][1]} not found`);
                    } else if (data.length > 1) {
                        message.channel.send(`Multiple matches`);
                    } else {
                        message.channel.send(`Character set to ${data[0].name}`);
                        state.setCharacterId(data[0].id);
                    }
                })
        } else {
            findCharacterById(characterId)
                .then(data => {
                    if (data.length < 1) {
                        message.channel.send(`Character is id of ${characterId} not found`);
                    } else if (data.length > 1) {
                        message.channel.send(`Seriously, how did you get here?`);
                    } else {
                        message.channel.send(`Character set to ${data[0].name}`);
                        state.setCharacterId(characterId);
                    }
                });
        }
    } else {
        message.channel.send("Character data must be an integer.");
    }
}

function getCharacters() {
    return fetch(`https://www.loot.senorpez.com/campaigns/${state.getCampaignId()}/characters`)
        .then(response => response.json());
}

function findCharacterById(characterId) {
    return getCharacters()
        .then(data => data._embedded['loot-api:character'].filter(character => character.id === characterId));
}

function findCharacterByName(characterName) {
    return getCharacters()
        .then(data => data._embedded['loot-api:character'].filter(character => character.name.toLowerCase().includes(characterName.toLowerCase())));
}