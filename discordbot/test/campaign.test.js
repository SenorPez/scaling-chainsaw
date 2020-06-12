const assert = require('chai').assert;
const {getCampaigns, findCampaignById, findCampaignByName} = require('../commands/campaign');

const mockResponse = {hello: 'world'}
const mockIndex = {
    _links: {
        'loot-api:campaigns': {
            href: 'http://mockserver/campaigns/'
        }
    }
};
const mockCampaigns = {
    _embedded: {
        'loot-api:campaign': [{
            id: 1,
            name: 'Test Campaign',
            _links: {
                self: {
                    href: 'http://mockserver/campaigns/1/'
                }
            }
        }]
    }
};


suite('Mock API', function () {
    test('Should return valid mock Campaigns JSON', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
            mockIndex
        );
        fetchMock.mock(
            'http://mockserver/campaigns/',
            mockResponse
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {getCampaigns} = proxyquire('../commands/campaign', {'../service/api': api});

        return getCampaigns()
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
                assert.isOk(fetchMock.done());
            })
    });

    test('Should return valid mock Campaign JSON, matched by ID', function () {
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
            mockResponse
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignById} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignById(1)
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
                assert.isOk(fetchMock.done());
            });
    });

    test('Should return valid mock Campaign JSON, matched by exact name', function () {
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
            mockResponse
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('Test Campaign')
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
                assert.isOk(fetchMock.done());
            });
    });

    test('Should return valid mock Campaign JSON, matched by differently cased name', function () {
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
            mockResponse
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('tESt CAmpaIGn')
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
                assert.isOk(fetchMock.done());
            });
    });

    test('Should return valid mock Campaign JSON, matched by partial name', function () {
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
            mockResponse
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});
        const {findCampaignByName} = proxyquire('../commands/campaign', {'../service/api': api});

        return findCampaignByName('test')
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
                assert.isOk(fetchMock.done());
            });
    });
});


suite('Reference API', function () {
    test('Should return valid Campaigns JSON', function () {
        return getCampaigns()
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(data => {
                assert.hasAllKeys(data, ['_embedded', '_links']);
                assert.hasAllKeys(data._embedded, ['loot-api:campaign']);
                data._embedded['loot-api:campaign'].forEach(campaign => {
                    assert.hasAllKeys(campaign, ['id', 'name', '_links']);
                    assert.hasAllKeys(campaign._links, ['self']);
                    assert.hasAllKeys(campaign._links['self'], ['href']);
                    assert.isString(campaign._links['self'].href);
                })
            });
    });

    test('Should return valid Campaign JSON, matched by ID', function () {
        return findCampaignById(1)
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name', '_links']);
                assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
            });
    })

    test('Rejection, ID not found', function () {
        return findCampaignById(8675309)
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(() => {
                    assert.fail();
                },
                rejection => {
                    assert.isOk(rejection.constructor.name, "CampaignIdNotFoundError");
                    assert.hasAllKeys(rejection, ['campaignId']);
                    assert.isOk(rejection.campaignId, 8675309);
                });
    });

    test('Should return valid Campaign JSON, matched by exact name', function () {
        return findCampaignByName('Defiance in Phlan')
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name', '_links']);
                assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
            })
    });

    test('Should return valid Campaign JSON, matched by differently cased name', function () {
        return findCampaignByName('dEFianCe iN pHLan')
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name', '_links']);
                assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
            });
    });

    test('Should return valid Campaign JSON, matched by partial name', function () {
        return findCampaignByName('phlan')
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(data => {
                assert.hasAllKeys(data, ['id', 'name', '_links']);
                assert.hasAllKeys(data._links, ['self', 'loot-api:campaigns', 'loot-api:characters', 'index', 'curies']);
            });
    });

    test('Rejection, no matches', function () {
        return findCampaignByName('erewqasdfadsger')
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(() => {
                    assert.fail();
                },
                rejection => {
                    assert.isOk(rejection.constructor.name, "CampaignNotFoundError");
                    assert.hasAllKeys(rejection, ['campaignName']);
                });
    });

    test('Rejection, multiple matches', function () {
        return findCampaignByName('e')
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(() => {
                    assert.fail();
                },
                rejection => {
                    assert.isOk(rejection.constructor.name, "MultipleMatchError");
                    assert.hasAllKeys(rejection, ['data']);
                    rejection.data.forEach(val => assert.hasAllKeys(val, ['id', 'name', '_links']))
                });
    });
});