const assert = require('chai').assert;
const sinon = require('sinon');

const {parseMessage, parseArguments, setCharacter} = require('../commands/character');
const state = require('../service/state');

const mockCampaign = {
    id: 1,
    name: 'Test Campaign',
    _links: {
        'loot-api:characters': {href: 'http://mockserver/campaigns/1/characters/'}
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
    name: 'Vorgansharanx',
    inventory: [
        {
            name: 'Gold Piece',
            quantity: 420
        }
    ]
};

suite('Mock API', function () {
    setup(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    });

    teardown(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    });

    test('parseMessage: Campaign not set', function () {
        const mockMessage = {content: '$character Vorgansharax'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Campaign not set; use $campaign to set"));
    });

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
            .then(numberId => assert.strictEqual(numberId, 8675309));
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
            .then(characters => {
                assert.containsAllKeys(characters, '_embedded');
                assert.containsAllKeys(characters._embedded, 'loot-api:character');
                characters._embedded['loot-api:character'].forEach(character => {
                    assert.containsAllKeys(character, ['id', 'name', '_links']);
                    assert.containsAllKeys(character._links, 'self');
                    assert.containsAllKeys(character._links.self, 'href');
                });
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
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                });
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
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
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
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
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
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
            });
    });

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
    });

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
});

suite('Local API', function () {
    setup(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    });
    
    teardown(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    })

    test('parseMessage: Regex match, valid command', function () {
        state.setCampaignId(1);
        const mockMessage = {content: '$character Aethelwuf'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    });

    test('parseMessage: Campaign not set', function () {
        const mockMessage = {content: '$character Aethelwuf'};
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, 'Campaign not set; use $campaign to set'));
    })

    test('parseMessage: No regex match, invalid command', function () {
        state.setCampaignId(1);
        const mockMessage = {content: 'fail'}
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Usage: $character <character name>"));
    });

    test('parseArguments: Search argument is text', function () {
        const mockMatches = [null, 'Aethelwuf'];
        return parseArguments(mockMatches)
            .then(textId => assert.strictEqual(textId, 'Aethelwuf'));
    });

    test('parseArguments: Search argument is number', function () {
        const mockMatches = [null, '1'];
        return parseArguments(mockMatches)
            .then(numberId => assert.strictEqual(numberId, 1));
    });

    test('getCharacter: Valid Characters JSON', function () {
        state.setCampaignId(1);
        const {getCharacters} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return getCharacters()
            .then(response => response.json())
            .then(characters => {
                assert.containsAllKeys(characters, '_embedded');
                assert.containsAllKeys(characters._embedded, 'loot-api:character');
                characters._embedded['loot-api:character'].forEach(character => {
                    assert.containsAllKeys(character, ['id', 'name', '_links']);
                    assert.containsAllKeys(character._links, 'self');
                    assert.containsAllKeys(character._links.self, 'href');
                });
            });
    });

    test('findCharacterByName: Valid Character JSON, matched by exact name', function () {
        state.setCampaignId(1);
        const {findCharacterByName} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return findCharacterByName('Aethelwuf')
            .then(response => response.json())
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
            });
    });

    test('findCharacterByName: Valid Character JSON, matched by differently cased name', function () {
        state.setCampaignId(1);
        const {findCharacterByName} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return findCharacterByName('aETHElWuF')
            .then(response => response.json())
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
            });
    });

    test('findCharacterByName: Valid Character JSON, matched by partial name', function () {
        state.setCampaignId(1);
        const {findCharacterByName} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return findCharacterByName('Aethel')
            .then(response => response.json())
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
            });
    });

    test('findCharacterByName: No text match', function () {
        state.setCampaignId(1);
        const {findCharacterByName} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return findCharacterByName('leeadamaisfat')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(error.message, 'Character containing \'leeadamaisfat\' not found'));
    });

    test('findCharacterByName: Multiple text matches', function () {
        state.setCampaignId(1);
        const {findCharacterByName} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return findCharacterByName('h')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(error.message, 'Multiple characters containing \'h\' found:\nID: 1 Name: Aethelwuf\nID: 4 Name: Salith'));
    });

    test('findCharacterById: Valid Character JSON, matched by ID', function () {
        state.setCampaignId(1);
        const {findCharacterById} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return findCharacterById(1)
            .then(response => response.json())
            .then(character => {
                assert.containsAllKeys(character, ['id', 'name', 'inventory']);
                character.inventory.forEach(item => {
                    assert.containsAllKeys(item, ['name', 'quantity']);
                })
            });
    });

    test('findCampaignById: No ID match', function () {
        state.setCampaignId(1);
        const {findCharacterById} = require('../commands/character');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        return findCharacterById(8675309)
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(error.message, 'Character with ID of 8675309 not found'));
    });

    test('setCharacter', function () {
        const mockCharacter = {
            "id": 1,
            "name": "Aethelwuf",
            "inventory": [
                {
                    "name": "Trinket",
                    "quantity": 1,
                    "charges": null
                }
            ],
            "_links": {
                "self": {
                    "href": "https://localhost:9090/campaigns/1/characters/1"
                },
                "loot-api:characters": {
                    "href": "https://localhost:9090/campaigns/1/characters"
                },
                "index": {
                    "href": "https://localhost:9090"
                },
                "curies": [
                    {
                        "href": "https://localhost:9090/docs/reference.html#resources-loot-{rel}",
                        "name": "loot-api",
                        "templated": true
                    }
                ]
            }
        }
        const mockJson = sinon.stub().returns(Promise.resolve(mockCharacter));
        const mockResponse = {json: mockJson};

        const mockSend = sinon.stub();
        const mockMessage = {
            channel: {send: mockSend}
        };

        state.setCampaignId(1);
        state.setCharacterId(1);

        return setCharacter(mockResponse, mockMessage)
            .then(() => {
                sinon.assert.calledWith(mockSend, 'Character set to Aethelwuf');
                assert.strictEqual(state.getCampaignId(), 1);
                assert.strictEqual(state.getCharacterId(), mockCharacter.id);
            });
    });

    test('Character integration test: Malformed command', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        const character = require('../commands/character');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$asdfasdfadsf',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);

        return character(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Usage: $character <character name>');
            });
    });

    test('Character integration test: Campaign not set', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        const character = require('../commands/character');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$asdfasdfadsf',
            channel: {send: mockSend}
        };

        return character(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Campaign not set; use $campaign to set');
            });
    });

    test('Character integration test: Text not found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        const character = require('../commands/character');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$character leeadamaisfat',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);

        return character(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Character containing \'leeadamaisfat\' not found');
            });
    });

    test('Character integration test: Multiple text matches found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        const character = require('../commands/character');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$character h',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);

        return character(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Multiple characters containing \'h\' found:\nID: 1 Name: Aethelwuf\nID: 4 Name: Salith');
            });
    });

    test('Character integration test: ID not found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        const character = require('../commands/character');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$character 8675309',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);

        return character(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Character with ID of 8675309 not found');
            });
    });

    test('Character integration test: Success with text', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        const character = require('../commands/character');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$character Aethelwuf',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);

        return character(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Character set to Aethelwuf');
                assert.strictEqual(state.getCharacterId(), 1);
                assert.strictEqual(state.getCampaignId(), 1);
            });
    });
    state.setCampaignId(1);

    test('Character integration test: Success with ID', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'https://localhost:9090';

        const character = require('../commands/character');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$character 1',
            channel: {send: mockSend}
        };
        state.setCampaignId(1);

        return character(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Character set to Aethelwuf');
                assert.strictEqual(state.getCampaignId(), 1);
                assert.strictEqual(state.getCharacterId(), 1);
            });
    });
})