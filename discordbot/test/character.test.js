const assert = require('chai').assert;
const {parseMessage, parseArguments} = require('../commands/character');

suite('Mock API', function() {
    test('parseMessage: Regex match, valid command', function () {
        const mockMessage = {content: '$character Vorgansharax'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    });

    test('parseMessage: No regex match, invalid command', function () {
        const mockMessage = {content: 'fail'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Usage: $character <character name>"));
    });

    test('parseArguments: Search argument is text', function () {
        const mockMatches = [null, 'Search'];
        return parseArguments(mockMatches)
            .then(textId => assert.strictEqual(textId, 'Search'),
                () => assert.fail());
    });

    test('parseArguments: Search argument is number', function () {
        const mockMatches = [null, '8675309'];
        return parseArguments(mockMatches)
            .then(() => assert.fail(),
                (numberId) => assert.strictEqual(numberId, 8675309));
    });
})