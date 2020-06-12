const assert = require('chai').assert;
const {getCampaigns, findCampaignById} = require('../commands/campaign');

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



suite('Mock API', function() {
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
        const { getCampaigns } = proxyquire('../commands/campaign', {'../service/api': api});

        return getCampaigns()
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
                assert.isOk(fetchMock.done());
            })
    });

    test('Should return valid mock Campaign JSON', function() {
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
            })

    })
})

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

    test('Should return valid Campaign JSON', function () {
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
});