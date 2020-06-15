require("dotenv").config();
const regex = /^.+?\s+(\d+(?=\s))?\s*(.+?)(?=\s+(--.+)|\s*$)/g;
const argregex = /--(\S)\s+(.+?)(?=\s+--|\s*$)/g;
const fetch = require("node-fetch");

const api = require('../service/api')
const state = require('../service/state');

const getToken = require('../service/authtoken');

const args = {
    'r': null
};

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
    const tokenPromise = getToken();
    const itemPromise = module.exports.parseMessage(message)
        .then(matches => module.exports.parseArguments(matches))
        .then(
            searchParam => module.exports.findItemByName(searchParam),
            searchParam => module.exports.findItemById(searchParam)
        );
    const argsPromise = module.exports.parseMessage(message)
        .then(matches => module.exports.parseCommand(matches));

    Promise.all([tokenPromise, itemPromise, argsPromise])
        .then(values => {
            const item = values[1];
            const arguments = values[2];
            const token = values[0];
            module.exports.postTransaction(message, item, arguments, token);
        });
};

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
    });
};

module.exports.parseCommand = (matches) => {
    return new Promise((resolve, reject) => {
        /*
        Resolve: Text search for item
        Reject: Lookup item by id
         */
        const itemId = Number(matches[2]).valueOf();
        isNaN(itemId) ? resolve(matches[2]) : reject(itemId);
    });
};

module.exports.parseArguments = (matches) => {
    for (const key of Object.keys(args)) {
        args[key] = null;
    }

    const parsed = {
        quantity: matches[1] ? matches[1] : 1,
        arguments: matches[3]
    }

    return new Promise(resolve => {
        if (parsed.arguments === null) {
            parsed.arguments = args;
            resolve(parsed);
        }

        const matches = [...parsed.arguments.matchAll(argregex)];
        matches.forEach(match => {
            if (match[1] in args) {
                args[match[1]] = match[2];
            }
        });
        parsed.arguments = args;
        resolve(parsed);
    });
};

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
};

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
};

module.exports.postTransaction = (message, item, arguments, token) => {
    const newTransaction = {
        item: item.id,
        quantity: arguments.quantity,
        remark: arguments.arguments.r
    };
    const authHeader = `bearer ${token}`;

    return new Promise(resolve => {
        fetch(`https://www.loot.senorpez.com/campaigns/${state.getCampaignId()}/characters/${state.getCharacterId()}/itemtransactions/`, {
            method: "post",
            body: JSON.stringify(newTransaction),
            headers: {
                "Content-Type": "application/hal+json",
                "Authorization": authHeader,
            }
        })
            .then(response => response.json())
            .then(character => {
                updatedItem = character.inventory.filter(inventoryItem => inventoryItem.name === item.name);
                arguments.quantity > 0
                    ? message.channel.send(`Added ${arguments.quantity} ${item.name} to ${character.name}\nNow has ${updatedItem[0].quantity} ${updatedItem[0].name}`)
                    : message.channel.send(`Dropped ${arguments.quantity} ${item.name} from ${character.name}\nNow has ${updatedItem[0].quantity} ${updatedItem[0].name}`);
                resolve(character);
            });
    });
};