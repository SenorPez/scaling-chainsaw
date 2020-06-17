const assert = require('chai').assert;
const url = 'https://www.loot.senorpez.com/'

suite('Mock API', function() {
    test('Should return valid mock JSON', function() {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
            {
                _links: {
                    'loot-api:campaigns': {href: 'http://mockserver/campaigns/'},
                    'loot-api:lootitems': {href: 'http://mockserver/items/'}
                }
            }
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        return api.get(url)
            .then(response => response.json())
            .then(data => {
                assert.containsAllKeys(data, '_links');
                assert.containsAllKeys(data._links, ['loot-api:campaigns', 'loot-api:lootitems']);
                assert.containsAllKeys(data._links['loot-api:campaigns'], 'href');
                assert.containsAllKeys(data._links['loot-api:lootitems'], 'href');
            });
    });
});

suite('Local API', function () {
    test('Should return valid index JSON', function () {
        const api = require('../service/api');
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

        return api.get('https://localhost:9090')
            .then(response => response.json())
            .then(data => {
                assert.containsAllKeys(data, '_links');
                assert.containsAllKeys(data._links, ['loot-api:campaigns', 'loot-api:lootitems']);
                assert.containsAllKeys(data._links['loot-api:campaigns'], 'href');
                assert.containsAllKeys(data._links['loot-api:lootitems'], 'href');
            });
    });
});
