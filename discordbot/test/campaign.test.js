const assert = require('chai').assert;
const api = require('../service/api')

describe('api.js mocked', function () {
    it('return valid mock JSON', function () {
        const proxyquire = require('proxyquire')
        const fetchMock = require('fetch-mock').sandbox();
        fetchMock.mock(
            '*',
            {hello: 'world'});
        const api = proxyquire('../service/api', {'node-fetch': fetchMock});

        return api.getIndex()
            .then(response => response.json())
            .then(data => {
                assert.hasAllKeys(data, ['hello']);
            });
    });
});

describe('api.js', function () {
    it('returns valid index JSON', function () {
        return api.getIndex()
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
