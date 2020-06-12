const assert = require('chai').assert;
const api = require('../service/api')
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

suite('Reference API', function() {
    test('Should return valid Index JSON', function () {
        return api.get(url)
            .then(response => {
                assert.strictEqual(response.status, 200);
                return response.json();
            })
            .then(data => {
                assert.hasAllKeys(data, ['_links']);
                assert.hasAllKeys(data._links, ['self', 'index', 'loot-api:campaigns', 'loot-api:lootitems', 'curies']);
                assert.hasAllKeys(data._links['loot-api:campaigns'], "href");
            });
    });
});
