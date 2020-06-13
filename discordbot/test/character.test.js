const assert = require('chai').assert;
const sinon = require('sinon');

const {parseMessage, parseArguments, setCharacter} = require('../commands/character');
const state = require('../service/state');

const mockCampaign = {
    id: 1,
    name: 'Test Campaign',
    _links: {
        'loot-api:characters': {
            href: 'http://mockserver/campaigns/1/characters/'
        }
    }
};
const mockCharacters = {
    _embedded: {
        'loot-api:character': [
            {
                id: 1,
                name: 'Vorgansharanx',
                _links: {
                    self: {
                        href: 'http://mockserver/campaigns/1/characters/1/'
                    }
                }
            },
            {
                id: 2,
                name: 'Kai Ithor',
                _links: {
                    self: {
                        href: 'http://mockserver/campaigns/1/characters/2/'
                    }
                }
            }
        ]
    }
};
const mockCharacter = {
    id: 1,
    name: 'Vorgansharanx'
};

suite('Mock API', function() {
    setup(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    });

    teardown(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    })

    test('parseMessage: Campaign not set', function() {
        const mockMessage = {content: '$character Vorgansharax'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Campaign not set; use $campaign to set"));
    })

    test('parseMessage: Regex match, valid command', function () {
        state.setCampaignId(1);
        const mockMessage = {content: '$character Vorgansharax'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    });

    test('parseMessage: No regex match, invalid command', function () {
        state.setCampaignId(1);
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

    test('getCharacters: Valid Characters JSON', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {getCharacters} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return getCharacters()
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['_embedded']);
                assert.isOk(fetchMock.done())
            });
    });

    test('findCharacterByName: Valid Character JSON, matched by exact name', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/1/',
            mockCharacter
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {findCharacterByName} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return findCharacterByName('Vorgansharanx')
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCharacterByName: Valid Character JSON, matched by differently cased name', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/1/',
            mockCharacter
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {findCharacterByName} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return findCharacterByName('VoRGAnsHaRAnx')
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCharacterByName: Valid Character JSON, matched by exact name', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/1/',
            mockCharacter
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {findCharacterByName} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return findCharacterByName('Vorg')
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCharacterByName: No text match', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/1/',
            mockCharacter
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {findCharacterByName} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return findCharacterByName('leeadamaisfat')
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, 'Character containing \'leeadamaisfat\' not found'));
    });

    test('findCharacterByName: Multiple text matches', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/1/',
            mockCharacter
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {findCharacterByName} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return findCharacterByName('a')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(
                error.message,
                'Multiple characters containing \'a\' found:\nID: 1 Name: Vorgansharanx\nID: 2 Name: Kai Ithor'
            ));
    });

    test('findCharacterById: Valid Character JSON, matched by ID', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/1/',
            mockCharacter
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {findCharacterById} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return findCharacterById(1)
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    })

    test('findCharacterById: No ID match', function () {
        const proxyquire = require('proxyquire');
        const fetchMock = require('fetch-mock').sandbox();

        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/',
            mockCharacters
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/characters/1/',
            mockCharacter
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        const mockCampaignJson = sinon.stub().resolves(mockCampaign);
        const mockCampaignResponse = {json: mockCampaignJson};
        const mockFindCampaignById = sinon.stub().resolves(mockCampaignResponse);

        const {findCharacterById} = proxyquire('../commands/character', {
            '../commands/campaign': {
                findCampaignById: mockFindCampaignById
            }, '../service/api': api
        });

        return findCharacterById(8675309)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Character with ID of 8675309 not found"));
    })

    test('setCharacter', function () {
        const mockJson = sinon.stub().resolves(mockCharacter);
        const mockResponse = {json: mockJson};

        const mockSend = sinon.stub();
        const mockMessage = {
            channel: {send: mockSend}
        };

        state.setCharacterId(8675309);
        return setCharacter(mockResponse, mockMessage)
            .then(() => {
                assert.strictEqual(state.getCharacterId(), mockCharacter.id);
            });
    });
})
