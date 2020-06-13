const assert = require('chai').assert;
const {parseMessage} = require('../commands/character');

suite('Mock API', function() {
    test('parseMessage: Regex match, valid command', function () {
        const mockMessage = {content: '$character Vorgansharax'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    })

    test('parseMessage: No regex match, invalid command', function () {
        const mockMessage = {content: 'fail'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Usage: $character <character name>"));
    })
})