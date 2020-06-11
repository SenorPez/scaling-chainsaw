const fetch = require("node-fetch");

module.exports.getIndex = getIndex;

function getIndex() {
    return fetch("https://www.loot.senorpez.com/")
}
