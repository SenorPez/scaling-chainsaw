const sinon = require('sinon');
const state = require('../service/state');

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
        process.env.API_URL = 'https://localhost:9090/';

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
                sinon.assert.calledWith(mockSend, 'Usage: $additem <item name> [--r <remark>]');
            });
    });

    test('Drop item integration test: Campaign not set', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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
        process.env.API_URL = 'https://localhost:9090/';

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

})