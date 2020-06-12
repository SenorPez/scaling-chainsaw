const assert = require('chai').assert;
const { getCampaigns } = require('../commands/campaign');

suite('Mock API', function() {
    test('Should return valid mock JSON', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
            {
                _links:
                    {
                        'loot-api:campaigns': {
                            href: 'https://www.loot.senorpez.com/campaigns/'
                        }
                    }
            }
        );
        fetchMock.mock(
            'https://www.loot.senorpez.com/campaigns/',
            {hello: 'world'}
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
});