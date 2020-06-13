const regex = /^.+?(?:\s)+(.+)/g;
const fetch = require("node-fetch");

const api = require('../service/api');
const {findCampaignById} = require('../commands/campaign');
const state = require('../service/state');

function ParseError() {
    this.message = "Usage: $character <character name>";
}

function CharacterNotFoundError(characterName) {
    this.message = `Character containing '${characterName}' not found`;
}

function MultipleMatchError(characterName, data) {
    this.message = `Multiple characters containing '${characterName}' found:`;
    data.forEach(character => this.message = this.message + `\nID: ${character.id} Name: ${character.name}`);
}

function CharacterIdNotFoundError(characterId) {
    this.message = `Character with ID of ${characterId} not found`;
}

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

module.exports.parseMessage = (message) => {
    return new Promise(resolve => {
        const matches = [...message.content.matchAll(regex)];

        if (matches[0]) {
            resolve(matches[0]);
        }
        throw new ParseError();
    })
}

module.exports.parseArguments = (matches) => {
    return new Promise((resolve, reject) => {
        /*
        Resolve: Text search for campaign
        Reject: Lookup campaign by id
         */
        const characterId = Number(matches[1]).valueOf();
        isNaN(characterId) ? resolve(matches[1]) : reject(characterId);
    });
}

module.exports.getCharacters = () => {
    return findCampaignById(state.getCampaignId())
        .then(response => response.json())
        .then(campaign => api.get(campaign._links['loot-api:characters'].href));
}

module.exports.findCharacterByName = (characterName) => {
    return module.exports.getCharacters()
        .then(response => response.json())
        .then(characters => {
            const embeddedCharacter = characters._embedded['loot-api:character'].filter(character => character.name.toLowerCase().includes(characterName.toLowerCase()));
            if (embeddedCharacter.length === 1) {
                return api.get(embeddedCharacter.pop()._links.self.href);
            } else if (embeddedCharacter.length < 1) {
                throw new CharacterNotFoundError(characterName);
            }
            throw new MultipleMatchError(characterName, embeddedCharacter);
        });
}

module.exports.findCharacterById = (characterId) => {
    return module.exports.getCharacters()
        .then(response => response.json())
        .then(characters => {
            const embeddedCharacter = characters._embedded['loot-api:character'].filter(embeddedCharacter => embeddedCharacter.id === characterId);
            if (embeddedCharacter.length === 1) {
                return api.get(embeddedCharacter.pop()._links.self.href);
            }
            throw new CharacterIdNotFoundError(characterId);
        })
}

module.exports.setCharacter = (character, message) => {
    return character.json()
        .then(characterData => {
            message.channel.send(`Character set to ${characterData.name}`);
            state.setCharacterId(characterData.id);
        })
}