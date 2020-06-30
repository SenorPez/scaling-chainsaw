const assert = require('chai').assert;
const sinon = require('sinon');

const {parseMessage, parseCommand, parseArguments} = require('../commands/additem');
const state = require('../service/state');

const mockIndex = {
    _links: {
        'loot-api:campaigns': {href: 'http://mockserver/campaigns/'},
        'loot-api:lootitems': {href: 'http://mockserver/items/'}
    }
};
const mockItems = {
    _embedded: {
        'loot-api:lootitem': [
            {
                id: 1,
                name: 'Gold Piece',
                _links: {
                    self: {
                        href: 'http://mockserver/items/1/'
                    }
                }
            },
            {
                id: 2,
                name: 'Magic Item',
                _links: {
                    self: {
                        href: 'http://mockserver/items/2/'
                    }
                }
            }
        ]
    }
};
const mockItem = {
    id: 1,
    name: 'Gold Piece'
};
const mockCharacter = {
    id: 1,
    name: 'Vorgansharanx',
    inventory: [
        {
            id: 1,
            name: 'Gold Piece',
            quantity: 420
        }
    ]
};

function ParseError() {
    this.message = "Usage: $dropitem <item name> [--r <remark>]";
}

suite('Mock API', function() {
    setup(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
        process.env.API_URL = 'http://mockserver/';
    })

    teardown(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    })

    test('parseMessage: Campaign not set', function () {
        const mockMessage = {content: '$dropitem Gold Piece'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Campaign not set; use $campaign to set"));
    });

    test('parseMessage: Character not set', function () {
        const mockMessage = {content: '$dropitem Gold Piece'};
        state.setCampaignId(1);
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Character not set; use $character to set"));
    });

    test('parseMessage: Regex match, valid command', function () {
        state.setCampaignId(1);
        state.setCharacterId(1);
        const mockMessage = {content: '$dropitem Gold Piece'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    });

    test('parseMessage: No regex match, invalid command', function () {
        state.setCampaignId(1);
        state.setCharacterId(1);
        const mockMessage = {content: 'fail'};
        return parseMessage(mockMessage, new ParseError())
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Usage: $dropitem <item name> [--r <remark>]"));
    });

    test('parseCommand: Search argument is text', function () {
        const mockMatches = [null, null, 'Search', null];
        return parseCommand(mockMatches)
            .then(result => assert.strictEqual(result, 'Search'));
    });

    test('parseCommand: Search argument is number', function () {
        const mockMatches = [null, null, '8675309', null];
        return parseCommand(mockMatches)
            .then(result => assert.strictEqual(result, 8675309));
    });

    test('parseArguments: Quantity is set, args are set', function () {
        const mockMatches = [null, 11, null, '--r Remark'];
        return parseArguments(mockMatches)
            .then(result => {
                assert.strictEqual(result.quantity, 11);
                assert.strictEqual(result.arguments.r, 'Remark');
            });
    });

    test('parseArguments: Quantity is set, args are blank', function () {
        const mockMatches = [null, 11, null, null];
        return parseArguments(mockMatches)
            .then(result => {
                assert.strictEqual(result.quantity, 11);
                assert.isNull(result.arguments.r);
            });
    });

    test('parseArguments: Quantity is defaulted, args are set', function () {
        const mockMatches = [null, null, null, '--r Remark'];
        return parseArguments(mockMatches)
            .then(result => {
                assert.strictEqual(result.quantity, 1);
                assert.strictEqual(result.arguments.r, 'Remark');
            });
    });

    test('parseArguments: Quantity is default, args are not set', function () {
        const mockMatches = [null, null, null, null];
        return parseArguments(mockMatches)
            .then(result => {
                assert.strictEqual(result.quantity, 1);
                assert.isNull(result.arguments.r);
            });
    });

    test('parseArguments: args single match', function () {
        const mockMatches = [null, 11, null, '--r Remark'];
        return parseArguments(mockMatches)
            .then(result => assert.strictEqual(result.arguments.r, 'Remark'));
    });

    test('parseArguments: args duplicate matches', function () {
        const mockMatches = [null, 11, null, '--r Remark --r Second Remark'];
        return parseArguments(mockMatches)
            .then(result => assert.strictEqual(result.arguments.r, 'Second Remark'));
    });

    test('parseArguments: No arguments', function () {
        const mockMatches = [null, 11, null, null];
        return parseArguments(mockMatches)
            .then(result => assert.isNull(result.arguments.r));
    });

    test('parseArguments: One match, one unsupported', function () {
        const mockMatches = [null, 11, null, '--r Remark --t Whatever'];
        return parseArguments(mockMatches)
            .then(result => assert.strictEqual(result.arguments.r, 'Remark'));
    });

    test('parseArguments: One unsupported, one match', function () {
        const mockMatches = [null, 11, null, '--t Whatever --r Remark'];
        return parseArguments(mockMatches)
            .then(result => assert.strictEqual(result.arguments.r, 'Remark'));
    });

    test('parseArguments: Malformed arguments', function () {
        const mockMatches = [null, 11, null, 'Error'];
        return parseArguments(mockMatches)
            .then(result => assert.isNull(result.arguments.r));
    })

    test('getItems: Valid Items JSON', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {getItems} = proxyquire('../commands/additem', {'../service/api': api});

        return getItems()
            .then(response => response.json())
            .then(items => {
                assert.containsAllKeys(items, '_embedded');
                assert.containsAllKeys(items._embedded, 'loot-api:lootitem');
                items._embedded['loot-api:lootitem'].forEach(item => {
                    assert.containsAllKeys(item, ['id', 'name', '_links']);
                    assert.containsAllKeys(item._links, 'self');
                    assert.containsAllKeys(item._links.self, 'href');
                });
            });
    });

    test('findItemByName: Valid Item JSON, matched by exact name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        fetchMock.mock(
            'http://mockserver/items/1/',
            mockItem
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findItemByName} = proxyquire('../commands/additem', {'../service/api': api});

        return findItemByName('Gold Piece')
            .then(response => response.json())
            .then(item => {
                assert.hasAllKeys(item, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemByName: Valid Item JSON, matched by differently cased name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        fetchMock.mock(
            'http://mockserver/items/1/',
            mockItem
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findItemByName} = proxyquire('../commands/additem', {'../service/api': api});

        return findItemByName('gOLd PiECe')
            .then(response => response.json())
            .then(item => {
                assert.hasAllKeys(item, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemByName: Valid Item JSON, matched by partial name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        fetchMock.mock(
            'http://mockserver/items/1/',
            mockItem
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findItemByName} = proxyquire('../commands/additem', {'../service/api': api});

        return findItemByName('Gold')
            .then(response => response.json())
            .then(item => {
                assert.hasAllKeys(item, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemByName: No text match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        fetchMock.mock(
            'http://mockserver/items/1/',
            mockItem
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findItemByName} = proxyquire('../commands/additem', {'../service/api': api});

        return findItemByName('leeadamaisfat')
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, 'Item containing \'leeadamaisfat\' not found'));
    });

    test('findItemByName: Multiple text matches', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        fetchMock.mock(
            'http://mockserver/items/1/',
            mockItem
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findItemByName} = proxyquire('../commands/additem', {'../service/api': api});

        return findItemByName('i')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(
                error.message,
                'Multiple items containing \'i\' found:\nID: 1 Name: Gold Piece\nID: 2 Name: Magic Item'
            ));
    });

    test('findItemById: Valid Item JSON, matched by ID', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        fetchMock.mock(
            'http://mockserver/items/1/',
            mockItem
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findItemById} = proxyquire('../commands/additem', {'../service/api': api});

        return findItemById(1)
            .then(response => response.json())
            .then(item => {
                assert.hasAllKeys(item, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemById: No ID match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/items/',
            mockItems
        );
        fetchMock.mock(
            'http://mockserver/items/1/',
            mockItem
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findItemById} = proxyquire('../commands/additem', {'../service/api': api});

        return findItemById(8675309)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Item with ID of 8675309 not found"));
    });

    test('findItemOnCharacter: Item found, sufficient quantity', function () {
        const proxyquire = require('proxyquire');
        const mockCharacterJson = sinon.stub().resolves(mockCharacter);
        const mockCharacterResponse = {json: mockCharacterJson}
        const mockFindCharacterById = sinon.stub().resolves(mockCharacterResponse);
        const {findItemOnCharacter} = proxyquire('../commands/dropitem', {
            '../commands/character': {
                findCharacterById: mockFindCharacterById
            }
        });

        return findItemOnCharacter(mockItem, 1)
            .then(item => {
                assert.hasAllKeys(item, ['id', 'name'])
            });
    });

    test('findItemOnCharacter: Item found, insufficient quantity', function () {
        const proxyquire = require('proxyquire');
        const mockCharacterJson = sinon.stub().resolves(mockCharacter);
        const mockCharacterResponse = {json: mockCharacterJson}
        const mockFindCharacterById = sinon.stub().resolves(mockCharacterResponse);
        const {findItemOnCharacter} = proxyquire('../commands/dropitem', {
            '../commands/character': {
                findCharacterById: mockFindCharacterById
            }
        });

        return findItemOnCharacter(mockItem, 1000000)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Cannot drop 1000000 Gold Piece; Vorgansharanx has 420 Gold Piece"));
    });

    test('findItemOnCharacter: Item not found', function () {
        const proxyquire = require('proxyquire');
        const mockCharacterJson = sinon.stub().resolves(mockCharacter);
        const mockCharacterResponse = {json: mockCharacterJson}
        const mockFindCharacterById = sinon.stub().resolves(mockCharacterResponse);
        const {findItemOnCharacter} = proxyquire('../commands/dropitem', {
            '../commands/character': {
                findCharacterById: mockFindCharacterById
            }
        });

        return findItemOnCharacter({id: 8675039, name: 'Jenny'}, 1)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Cannot drop 1 Jenny; Vorgansharanx has 0 Jenny"));
    });

    test('postTransaction: Valid updated JSON', function () {
        state.setCampaignId(6);
        state.setCharacterId(6);

        const mockSend = sinon.stub();
        const mockMessage = {
            channel: {send: mockSend}
        };
        const mockArgs = {
            quantity: 11,
            arguments: {r: 'Remark'}
        }

        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/campaigns/6/characters/6/itemtransactions/',
            mockCharacter
        );
        const {postTransaction} = proxyquire('../commands/additem', {'node-fetch': fetchMock});

        return postTransaction(mockMessage, mockItem, mockArgs, '12345')
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
            });
    });
})

suite('Local API', function () {
    this.timeout(5000); // Slow auth server

    setup(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    });

    teardown(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    });

    test('Drop item integration test: Malformed command', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const dropitem = require('../commands/dropitem');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$adsendasdfadsf',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        return dropitem(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Usage: $dropitem <item name> [--r <remark>]');
            });
    });

    test('Drop item integration test: Campaign not set', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const dropitem = require('../commands/dropitem');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem Item of Testing',
            channel: {send: mockSend}
        };

        return dropitem(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Campaign not set; use $campaign to set');
            });
    });

    test('Drop item integration test: Character not set', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const dropitem = require('../commands/dropitem');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem Item of Testing',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);

        return dropitem(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Character not set; use $character to set');
            });
    });

    test('Drop item integration test: Text not found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const dropitem = require('../commands/dropitem');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem leeadamaisfat',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        return dropitem(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Item containing \'leeadamaisfat\' not found');
            });
    });

    test('Drop item integration test: Multiple text matches found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const dropitem = require('../commands/dropitem');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem piece',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        return dropitem(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend,
                    'Multiple items containing \'piece\' found:\n' +
                    'ID: 21 Name: Gold Piece\n' +
                    'ID: 23 Name: Silver Piece\n' +
                    'ID: 24 Name: Copper Piece\n' +
                    'ID: 45 Name: Platinum Piece'
                );
            });
    });

    test('Drop item integration test: ID not found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const dropitem = require('../commands/dropitem');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem 8675309',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        return dropitem(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Item with ID of 8675309 not found');
            });
    });

    test('Drop item integration test: Success with text, default quantity', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem Item of Testing',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        const {findCharacterByName} = require('../commands/character');
        const dropitem = require('../commands/dropitem');
        return findCharacterByName('Aethelwuf')
            .then(response => response.json())
            .then(character => character.inventory.filter(item => item.name === 'Item of Testing').pop())
            .then(item => {
                return dropitem(mockMessage)
                    .finally(() => sinon.assert.calledWith(mockSend,
                        'Dropped 1 Item of Testing from Aethelwuf\n' +
                        `Now has ${item.quantity - 1} Item of Testing`));
            });
    });

    test('Drop item integration test: Success with ID, default quantity', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem 8377302',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        const {findCharacterByName} = require('../commands/character');
        const dropitem = require('../commands/dropitem');
        return findCharacterByName('Aethelwuf')
            .then(response => response.json())
            .then(character => character.inventory.filter(item => item.name === 'Item of Testing').pop())
            .then(item => {
                return dropitem(mockMessage)
                    .finally(() => sinon.assert.calledWith(mockSend,
                        'Dropped 1 Item of Testing from Aethelwuf\n' +
                        `Now has ${item.quantity - 1} Item of Testing`));
            });
    });

    test('Drop item integration test: Success with text, set quantity', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem 11 Item of Testing',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        const {findCharacterByName} = require('../commands/character');
        const dropitem = require('../commands/dropitem');
        return findCharacterByName('Aethelwuf')
            .then(response => response.json())
            .then(character => character.inventory.filter(item => item.name === 'Item of Testing').pop())
            .then(item => {
                return dropitem(mockMessage)
                    .finally(() => sinon.assert.calledWith(mockSend,
                        'Dropped 11 Item of Testing from Aethelwuf\n' +
                        `Now has ${item.quantity - 11} Item of Testing`));
            });
    });

    test('Drop item integration test: Success with ID, set quantity', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem 11 8377302',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        const {findCharacterByName} = require('../commands/character');
        const dropitem = require('../commands/dropitem');
        return findCharacterByName('Aethelwuf')
            .then(response => response.json())
            .then(character => character.inventory.filter(item => item.name === 'Item of Testing').pop())
            .then(item => {
                return dropitem(mockMessage)
                    .finally(() => sinon.assert.calledWith(mockSend,
                        'Dropped 11 Item of Testing from Aethelwuf\n' +
                        `Now has ${item.quantity - 11} Item of Testing`));
            });
    });

    test('Drop item integration test: Failure due to excessive drop quantity', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem 1000000 8377302',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        const {findCharacterByName} = require('../commands/character');
        const dropitem = require('../commands/dropitem');
        return findCharacterByName('Aethelwuf')
            .then(response => response.json())
            .then(character => character.inventory.filter(item => item.name === 'Item of Testing').pop())
            .then(item => {
                return dropitem(mockMessage)
                    .finally(() => sinon.assert.calledWith(mockSend,
                        `Cannot drop 1000000 Item of Testing; Aethelwuf has ${item.quantity} Item of Testing`));
            });
    });

    test('Drop item integration test: Failure due to no item', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090/';

        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$dropitem 7942588',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);
        state.setCharacterId(1);

        const {findCharacterByName} = require('../commands/character');
        const dropitem = require('../commands/dropitem');
        return findCharacterByName('Aethelwuf')
            .then(response => response.json())
            .then(character => character.inventory.filter(item => item.name === 'Item of Testing').pop())
            .then(() => {
                return dropitem(mockMessage)
                    .finally(() => sinon.assert.calledWith(mockSend,
                        `Cannot drop 1 Item of Failure; Aethelwuf has 0 Item of Failure`));
            });
    });
})