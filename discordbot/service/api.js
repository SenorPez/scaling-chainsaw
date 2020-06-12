const fetch = require("node-fetch");

module.exports.get = get;

function get(url) {
    return fetch(
        url,
        {
            headers: {
                'Accept': 'application/hal+json'
            }
        }
    );
}
