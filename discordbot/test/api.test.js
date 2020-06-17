const assert = require('chai').assert;
const url = 'https://www.loot.senorpez.com/'

suite('Mock API', function() {
    test('Should return valid mock JSON', function() {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            'https://www.loot.senorpez.com/',
            {hello: 'world'}
        );
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        return api.get(url)
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
                assert.isOk(fetchMock.done());
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
                assert.hasAllKeys(data, ['_links']);
                assert.hasAllKeys(data._links, [
                    'self',
                    'index',
                    'loot-api:campaigns',
                    'loot-api:lootitems',
                    'curies'
                ]);
                assert.hasAllKeys(data._links.self, 'href');
                assert.hasAllKeys(data._links.index, 'href');
                assert.hasAllKeys(data._links["loot-api:campaigns"], 'href');
                assert.hasAllKeys(data._links["loot-api:lootitems"], 'href');
                assert.hasAllKeys(data._links.curies[0], ['href', 'name', 'templated']);
            });
    });
});
