const assert = require('chai').assert;
const sinon = require('sinon');

const {parseMessage, parseCommand, parseArguments} = require('../commands/additem');
const state = require('../service/state');

const mockIndex = {
    _links: {
        'loot-api:lootitems': {
            href: 'http://mockserver/items/'
        }
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
}
const mockItemTransactions = {
    id: 1,
    name: 'Vorgansharanx',
    inventory: [
        {
            name: 'Gold Piece',
            quantity: 12
        }
    ]
}

suite('Mock API', function() {
    setup(function() {
        state.setCampaignId(null);
        state.setCharacterId(null);
    })

    teardown(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    })

    test('parseMessage: Campaign not set', function () {
        const mockMessage = {content: '$additem Gold Piece'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Campaign not set; use $campaign to set"));
    });

    test('parseMessage: Character not set', function () {
        const mockMessage = {content: '$additem Gold Piece'};
        state.setCampaignId(1);
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Character not set; use $character to set"));
    });

    test('parseMessage: Regex match, valid command', function () {
        state.setCampaignId(1);
        state.setCharacterId(1);
        const mockMessage = {content: '$additem Gold Piece'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    });

    test('parseMessage: No regex match, invalid command', function () {
        state.setCampaignId(1);
        state.setCharacterId(1);
        const mockMessage = {content: 'fail'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Usage: $additem <item name> [--r <remark>]"));
    });

    test('parseCommand: Search argument is text', function () {
        const mockMatches = [null, null, 'Search', null];
        return parseCommand(mockMatches)
            .then((result) => assert.strictEqual(result, 'Search'),
                () => assert.fail());
    });

    test('parseCommand: Search argument is number', function () {
        const mockMatches = [null, null, '8675309', null];
        return parseCommand(mockMatches)
            .then(() => assert.fail(),
                (result) => assert.strictEqual(result, 8675309));
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
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['_embedded']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemByName: Valid Item JSON, matched by exact name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemByName: Valid Item JSON, matched by differently cased name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemByName: Valid Item JSON, matched by partial name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemByName: No text match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            'https://www.loot.senorpez.com/',
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
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findItemById: No ID match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            'https://www.loot.senorpez.com/campaigns/6/characters/6/itemtransactions/',
            mockItemTransactions
        );
        const {postTransaction} = proxyquire('../commands/additem', {'node-fetch': fetchMock});

        return postTransaction(mockMessage, mockItem, mockArgs, '12345')
            .then(character => assert.strictEqual(character.inventory[0].quantity, 12));
    })
})