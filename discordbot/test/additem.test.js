const assert = require('chai').assert;

const {parseMessage, parseCommand, parseArguments} = require('../commands/additem');
const state = require('../service/state');

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
})