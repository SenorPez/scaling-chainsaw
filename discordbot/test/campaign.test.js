const assert = require('chai').assert;
const {parseMessage, parseArguments, setCampaign} = require('../commands/campaign');
const sinon = require('sinon');
const state = require('../service/state');

const mockIndex = {
    _links: {
        'loot-api:campaigns': {
            href: 'http://mockserver/campaigns/'
        }
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
    name: 'Test Campaign'
}

suite('Mock API', function () {
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
            .then(() => assert.fail(),
                (numberId) => assert.strictEqual(numberId, 8675309));
    });

    test('getCampaigns: Valid Campaigns JSON', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['_embedded']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by exact name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by differently cased name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCampaignByName: Valid Campaign JSON, matched by partial name', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCampaignByName: No text match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
            'https://www.loot.senorpez.com/',
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
            'https://www.loot.senorpez.com/',
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
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name']);
                assert.isOk(fetchMock.done());
            });
    });

    test('findCampaignById: No ID match', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
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
    })

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
    })
});

// suite('Reference API', function () {
//     test('Should return valid Campaigns JSON', function () {
//         return getCampaigns()
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(data => {
//                 assert.hasAllKeys(data, ['_embedded', '_links']);
//                 assert.hasAllKeys(data._embedded, ['loot-api:campaign']);
//                 data._embedded['loot-api:campaign'].forEach(campaign => {
//                     assert.hasAllKeys(campaign, ['id', 'name', '_links']);
//                     assert.hasAllKeys(campaign._links, ['self']);
//                     assert.hasAllKeys(campaign._links['self'], ['href']);
//                     assert.isString(campaign._links['self'].href);
//                 })
//             });
//     });
//
//     test('Should return valid Campaign JSON, matched by ID', function () {
//         return findCampaignById(1)
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(data => {
//                 assert.hasAllKeys(data, ['id', 'name', '_links']);
//                 assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
//             });
//     })
//
//     test('Rejection, ID not found', function () {
//         return findCampaignById(8675309)
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(() => {
//                     assert.fail();
//                 },
//                 rejection => {
//                     assert.isOk(rejection.constructor.name, "CampaignIdNotFoundError");
//                     assert.hasAllKeys(rejection, ['campaignId']);
//                     assert.isOk(rejection.campaignId, 8675309);
//                 });
//     });
//
//     test('Should return valid Campaign JSON, matched by exact name', function () {
//         return findCampaignByName('Defiance in Phlan')
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(data => {
//                 assert.hasAllKeys(data, ['id', 'name', '_links']);
//                 assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
//             })
//     });
//
//     test('Should return valid Campaign JSON, matched by differently cased name', function () {
//         return findCampaignByName('dEFianCe iN pHLan')
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(data => {
//                 assert.hasAllKeys(data, ['id', 'name', '_links']);
//                 assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
//             });
//     });
//
//     test('Should return valid Campaign JSON, matched by partial name', function () {
//         return findCampaignByName('phlan')
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(data => {
//                 assert.hasAllKeys(data, ['id', 'name', '_links']);
//                 assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
//             });
//     });
//
//     test('Rejection, no matches', function () {
//         return findCampaignByName('erewqasdfadsger')
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(() => {
//                     assert.fail();
//                 },
//                 rejection => {
//                     assert.isOk(rejection.constructor.name, "CampaignNotFoundError");
//                     assert.hasAllKeys(rejection, ['campaignName']);
//                 });
//     });
//
//     test('Rejection, multiple matches', function () {
//         return findCampaignByName('e')
//             .then(response => {
//                 assert.strictEqual(response.status, 200);
//                 return response.json();
//             })
//             .then(() => {
//                     assert.fail();
//                 },
//                 rejection => {
//                     assert.isOk(rejection.constructor.name, "MultipleMatchError");
//                     assert.hasAllKeys(rejection, ['data']);
//                     rejection.data.forEach(val => assert.hasAllKeys(val, ['id', 'name', '_links']))
//                 });
//     });
// });