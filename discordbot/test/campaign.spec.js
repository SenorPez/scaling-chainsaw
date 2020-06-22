const assert = require('chai').assert;
const {parseMessage, parseArguments, setCampaign} = require('../commands/campaign');
const sinon = require('sinon');
const state = require('../service/state');

const mockIndex = {
    _links: {
        'loot-api:campaigns': {href: 'http://mockserver/campaigns/'},
        'loot-api:lootitems': {href: 'http://mockserver/items/'}
    }
};
const mockCampaigns = {
    _embedded: {
        'loot-api:campaign': [
            {
                id: 1,
                name: 'Test Campaign',
                _links: {
                    self: {
                        href: 'http://mockserver/campaigns/1/'
                    }
                }
            },
            {
                id: 2,
                name: 'Mock Campaign',
                _links: {
                    self: {
                        href: 'http://mockserver/campaigns/2/'
                    }
                }
            }
        ]
    }
};
const mockCampaign = {
    id: 1,
    name: 'Test Campaign',
    _links: {
        'loot-api:characters': {href: 'http://mockserver/campaigns/1/characters/'}
    }
};

suite('Mock API', function () {
    setup(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
        process.env.API_URL = 'http://mockserver/';
    });

    teardown(function () {
        state.setCampaignId(null);
        state.setCharacterId(null);
    });

    test('parseMessage: Regex match, valid command', function () {
        const mockMessage = {content: '$campaign Defiance in Phlan'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    });

    test('parseMessage: No regex match, invalid command', function () {
        const mockMessage = {content: 'fail'}
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Usage: $campaign <campaign name>"));
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

    test('getCampaigns: Valid Campaigns JSON', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {getCampaigns} = proxyquire('../commands/campaign', {'../service/api': api});

        return getCampaigns()
            .then(response => response.json())
            .then(campaigns => {
                assert.containsAllKeys(campaigns, '_embedded');
                assert.containsAllKeys(campaigns._embedded, 'loot-api:campaign');
                campaigns._embedded['loot-api:campaign'].forEach(campaign => {
                    assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                    assert.containsAllKeys(campaign._links, 'self');
                    assert.containsAllKeys(campaign._links.self, 'href');
                });
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by exact name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/',
            mockCampaign
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('Test Campaign')
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by differently cased name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/',
            mockCampaign
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('tESt CAmpaIGn')
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by partial name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/',
            mockCampaign
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('test')
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignByName: No text match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/',
            mockCampaign
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('leeadamaisfat')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(error.message, 'Campaign containing \'leeadamaisfat\' not found'));
    });

    test('findCampaignByName: Multiple text matches', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/',
            mockCampaign
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('campaign')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(
                error.message,
                'Multiple campaigns containing \'campaign\' found:\nID: 1 Name: Test Campaign\nID: 2 Name: Mock Campaign'
            ));
    });

    test('findCampaignById: Valid Campaign JSON, matched by ID', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/',
            mockCampaign
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignById} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignById(1)
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignById: No ID match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'http://mockserver/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockCampaigns
        );
        fetchMock.mock(
            'http://mockserver/campaigns/1/',
            mockCampaign
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignById} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignById(8675309)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Campaign with ID of 8675309 not found"));
    });

    test('setCampaign: No character reset', function () {
        const mockJson = sinon.stub().returns(Promise.resolve(mockCampaign));
        const mockSend = sinon.stub();

        const mockResponse = {
            json: mockJson
        };
        const mockMessage = {
            channel: {
                send: mockSend
            }
        };

        state.setCampaignId(1);
        state.setCharacterId(8675309);

        return setCampaign(mockResponse, mockMessage)
            .then(() => {
                assert.strictEqual(state.getCampaignId(), mockCampaign.id);
                assert.strictEqual(state.getCharacterId(), 8675309);
            });
    });

    test('setCampaign: Character reset', function () {
        const mockJson = sinon.stub().returns(Promise.resolve(mockCampaign));
        const mockSend = sinon.stub();

        const mockResponse = {
            json: mockJson
        };
        const mockMessage = {
            channel: {
                send: mockSend
            }
        };

        state.setCampaignId(2);
        state.setCharacterId(8675309);

        return setCampaign(mockResponse, mockMessage)
            .then(() => {
                assert.strictEqual(state.getCampaignId(), mockCampaign.id);
                assert.strictEqual(state.getCharacterId(), null);
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
    });

    test('parseMessage: Regex match, valid command', function () {
        const mockMessage = {content: '$campaign Defiance in Phlan'};
        return parseMessage(mockMessage)
            .then(response => assert.isArray(response));
    });

    test('parseMessage: No regex match, invalid command', function () {
        const mockMessage = {content: 'fail'}
        return parseMessage(mockMessage)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Usage: $campaign <campaign name>"));
    });

    test('parseArguments: Search argument is text', function () {
        const mockMatches = [null, 'Search'];
        return parseArguments(mockMatches)
            .then(textId => assert.strictEqual(textId, 'Search'));
    });

    test('parseArguments: Search argument is number', function () {
        const mockMatches = [null, '8675309'];
        return parseArguments(mockMatches)
            .then(numberId => assert.strictEqual(numberId, 8675309));
    });

    test('getCampaign: Valid Campaigns JSON', function () {
        const {getCampaigns} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return getCampaigns()
            .then(response => response.json())
            .then(campaigns => {
                assert.containsAllKeys(campaigns, '_embedded');
                assert.containsAllKeys(campaigns._embedded, 'loot-api:campaign');
                campaigns._embedded['loot-api:campaign'].forEach(campaign => {
                    assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                    assert.containsAllKeys(campaign._links, 'self');
                    assert.containsAllKeys(campaign._links.self, 'href');
                });
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by exact name', function () {
        const {findCampaignByName} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return findCampaignByName('Defiance in Phlan')
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by differently cased name', function () {
        const {findCampaignByName} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return findCampaignByName('dEFiAnCE iN pHLAn')
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by partial name', function () {
        const {findCampaignByName} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return findCampaignByName('Phlan')
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignByName: No text match', function () {
        const {findCampaignByName} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return findCampaignByName('leeadamaisfat')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(error.message, 'Campaign containing \'leeadamaisfat\' not found'));
    });

    test('findCampaignByName: Multiple text matches', function () {
        const {findCampaignByName} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return findCampaignByName('p')
            .then(() => assert.fail())
            .catch((error) => assert.strictEqual(error.message, 'Multiple campaigns containing \'p\' found:\nID: 1 Name: Defiance in Phlan\nID: 3 Name: New Campaign'));
    });

    test('findCampaignById: Valid Campaign JSON, matched by ID', function () {
        const {findCampaignById} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return findCampaignById(1)
            .then(response => response.json())
            .then(campaign => {
                assert.containsAllKeys(campaign, ['id', 'name', '_links']);
                assert.containsAllKeys(campaign._links, 'loot-api:characters');
                assert.containsAllKeys(campaign._links['loot-api:characters'], 'href');
            });
    });

    test('findCampaignById: No ID match', function () {
        const {findCampaignById} = require('../commands/campaign');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        return findCampaignById(8675309)
            .then(() => assert.fail())
            .catch(error => assert.strictEqual(error.message, "Campaign with ID of 8675309 not found"));
    });

    test('setCampaign: No character reset', function () {
        const mockCampaign = {
            id: 1,
            name: "Defiance in Phlan",
            _links: {
                self: {href: "http://localhost:9090/campaigns/1"},
                'loot-api:campaigns': {href: "http://localhost:9090/campaigns"},
                'loot-api:characters': {href: "http://localhost:9090/campaigns/1/characters"},
                index: {href: "http://localhost:9090"},
                curies: [
                    {
                        href: "http://localhost:9090/docs/reference.html#resources-loot-{rel}",
                        name: "loot-api",
                        templated: true
                    }
                ]
            }
        };
        const mockJson = sinon.stub().returns(Promise.resolve(mockCampaign));
        const mockResponse = {json: mockJson};

        const mockSend = sinon.stub();
        const mockMessage = {
            channel: {send: mockSend}
        };

        state.setCampaignId(1);
        state.setCharacterId(8675309);

        return setCampaign(mockResponse, mockMessage)
            .then(() => {
                sinon.assert.calledWith(mockSend, 'Campaign set to Defiance in Phlan');
                assert.strictEqual(state.getCampaignId(), mockCampaign.id);
                assert.strictEqual(state.getCharacterId(), 8675309);
            });
    });

    test('setCampaign: Character reset', function () {
        const mockCampaign = {
            id: 1,
            name: "Defiance in Phlan",
            _links: {
                self: {href: "http://localhost:9090/campaigns/1"},
                'loot-api:campaigns': {href: "http://localhost:9090/campaigns"},
                'loot-api:characters': {href: "http://localhost:9090/campaigns/1/characters"},
                index: {href: "http://localhost:9090"},
                curies: [
                    {
                        href: "http://localhost:9090/docs/reference.html#resources-loot-{rel}",
                        name: "loot-api",
                        templated: true
                    }
                ]
            }
        };
        const mockJson = sinon.stub().returns(Promise.resolve(mockCampaign));
        const mockResponse = {json: mockJson};

        const mockSend = sinon.stub();
        const mockMessage = {
            channel: {send: mockSend}
        };

        state.setCampaignId(2);
        state.setCharacterId(8675309);

        return setCampaign(mockResponse, mockMessage)
            .then(() => {
                sinon.assert.calledWith(mockSend, 'Campaign set to Defiance in Phlan');
                assert.strictEqual(state.getCampaignId(), mockCampaign.id);
                assert.isNull(state.getCharacterId());
            });
    });

    test('Campaign integration test: Malformed command', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        const campaign = require('../commands/campaign');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$asdfasdfadsf',
            channel: {send: mockSend}
        };

        return campaign(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Usage: $campaign <campaign name>');
            });
    });

    test('Campaign integration test: Text not found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        const campaign = require('../commands/campaign');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$campaign leeadamaisfat',
            channel: {send: mockSend}
        };

        return campaign(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Campaign containing \'leeadamaisfat\' not found');
            });
    });

    test('Campaign integration test: Multiple text matches found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        const campaign = require('../commands/campaign');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$campaign p',
            channel: {send: mockSend}
        };

        return campaign(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Multiple campaigns containing \'p\' found:\nID: 1 Name: Defiance in Phlan\nID: 3 Name: New Campaign');
            });
    });

    test('Campaign integration test: ID not found', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        const campaign = require('../commands/campaign');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$campaign 8675309',
            channel: {send: mockSend}
        };

        return campaign(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Campaign with ID of 8675309 not found');
            });
    });

    test('Campaign integration test: Success with text', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        const campaign = require('../commands/campaign');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$campaign Defiance in Phlan',
            channel: {send: mockSend}
        };

        return campaign(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Campaign set to Defiance in Phlan');
                assert.strictEqual(state.getCampaignId(), 1);
                assert.isNull(state.getCharacterId());
            });
    });

    test('Campaign integration test: Success with ID', function () {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        process.env.API_URL = 'http://localhost:9090';

        const campaign = require('../commands/campaign');
        const mockSend = sinon.stub();
        const mockMessage = {
            content: '$campaign 1',
            channel: {send: mockSend}
        };

        return campaign(mockMessage)
            .finally(() => {
                sinon.assert.calledWith(mockSend, 'Campaign set to Defiance in Phlan');
                assert.strictEqual(state.getCampaignId(), 1);
                assert.isNull(state.getCharacterId());
            });
    });
})
