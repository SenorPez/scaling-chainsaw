const assert = require('chai').assert;
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

    test('parseCommand: Search argument is text, quantity is set, args are set', function () {
        const mockMatches = [null, 11, 'Search', '--r Remark'];
        return parseCommand(mockMatches)
            .then((result) => {
                    assert.strictEqual(result.id, 'Search');
                    assert.strictEqual(result.quantity, 11);
                    assert.strictEqual(result.args, '--r Remark');
                },
                () => assert.fail());
    });

    test('parseCommand: Search argument is text, quantity is set, args are not set', function () {
        const mockMatches = [null, 11, 'Search', null];
        return parseCommand(mockMatches)
            .then((result) => {
                    assert.strictEqual(result.id, 'Search');
                    assert.strictEqual(result.quantity, 11);
                    assert.strictEqual(result.args, null);
                },
                () => assert.fail());
    });

    test('parseCommand: Search argument is text, quantity is defaulted to 1, args are set', function () {
        const mockMatches = [null, null, 'Search', '--r Remark'];
        return parseCommand(mockMatches)
            .then((result) => {
                    assert.strictEqual(result.id, 'Search');
                    assert.strictEqual(result.quantity, 1);
                    assert.strictEqual(result.args, '--r Remark');
                },
                () => assert.fail());
    });

    test('parseCommand: Search argument is text, quantity is defaulted to 1, args are not set', function () {
        const mockMatches = [null, null, 'Search', null];
        return parseCommand(mockMatches)
            .then((result) => {
                    assert.strictEqual(result.id, 'Search');
                    assert.strictEqual(result.quantity, 1);
                    assert.strictEqual(result.args, null);
                },
                () => assert.fail());
    });

    test('parseCommand: Search argument is number, quantity is set, args are set', function () {
        const mockMatches = [null, 11, '8675309', '--r Remark'];
        return parseCommand(mockMatches)
            .then(() => assert.fail(),
                (result) => {
                    assert.strictEqual(result.id, 8675309);
                    assert.strictEqual(result.quantity, 11);
                    assert.strictEqual(result.args, '--r Remark');
                });
    });

    test('parseCommand: Search argument is number, quantity is set, args are not set', function () {
        const mockMatches = [null, 11, '8675309', null];
        return parseCommand(mockMatches)
            .then(() => assert.fail(),
                (result) => {
                    assert.strictEqual(result.id, 8675309);
                    assert.strictEqual(result.quantity, 11);
                    assert.strictEqual(result.args, null);
                });
    });

    test('parseCommand: Search argument is number, quantity is defaulted to 1, args are set', function () {
        const mockMatches = [null, null, '8675309', '--r Remark'];
        return parseCommand(mockMatches)
            .then(() => assert.fail(),
                (result) => {
                    assert.strictEqual(result.id, 8675309);
                    assert.strictEqual(result.quantity, 1);
                    assert.strictEqual(result.args, '--r Remark');
                });
    });

    test('parseCommand: Search argument is number, quantity is defaulted to 1, args are not set', function () {
        const mockMatches = [null, null, '8675309', null];
        return parseCommand(mockMatches)
            .then(() => assert.fail(),
                (result) => {
                    assert.strictEqual(result.id, 8675309);
                    assert.strictEqual(result.quantity, 1);
                    assert.strictEqual(result.args, null);
                });
    });

    test('parseArguments: Single match', function () {
        const mockArguments = '--r Remark';
        return parseArguments(mockArguments)
            .then(arguments => assert.strictEqual(arguments['r'], 'Remark'));
    });

    test('parseArguments: Duplicate matches', function () {
        const mockArguments = '--r Remark --r Second Remark';
        return parseArguments(mockArguments)
            .then(arguments => assert.strictEqual(arguments['r'], 'Second Remark'));
    });

    test('parseArguments: No arguments', function () {
        const mockArguments = null;
        return parseArguments(mockArguments)
            .then(arguments => assert.isNull(arguments['r']));
    });

    test('parseArguments: One match, one unsupported', function () {
        const mockArguments = '--r Remark --t Whatever';
        return parseArguments(mockArguments)
            .then(arguments => assert.strictEqual(arguments['r'], 'Remark'));
    });

    test('parseArguments: One unsupported, one match', function () {
        const mockArguments = '--t Whatever --r Remark';
        return parseArguments(mockArguments)
            .then(arguments => assert.strictEqual(arguments['r'], 'Remark'));
    });

    test('parseArguments: Malformed arguments', function () {
        const mockArguments = 'Error';
        return parseArguments(mockArguments)
            .then(arguments => assert.isNull(arguments['r']));
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
})