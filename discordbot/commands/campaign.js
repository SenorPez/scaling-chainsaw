const regex = /^.+?(?:\s)+(.+)/g
const fetch = require("node-fetch");

const state = require('../service/state');

module.exports = (message) => {
    const matches = [...message.content.matchAll(regex)];
    if (matches[0]) {
        const campaignId = Number(matches[0][1]).valueOf();
        if (isNaN(campaignId)) {
            findCampaignByName(matches[0][1])
                .then(data => {
                    if (data.length < 1) {
                        message.channel.send(`Campaign with name of ${matches[0][1]} not found.`);
                    } else if (data.length > 1) {
                        message.channel.send(`Multiple matches`);
                    } else {
                        message.channel.send(`Campaign set to ${data[0].name}`);
                        state.setCampaignId(data[0].id);
                    }
                });
        } else {
            findCampaignById(campaignId)
                .then(data => {
                    if (data.length < 1) {
                        message.channel.send(`Campaign with id of ${campaignId} not found.`);
                    } else if (data.length > 1) {
                        message.channel.send(`Seriously, how did you manage this?`);
                    } else {
                        message.channel.send(`Campaign set to ${data[0].name}`);
                        state.setCampaignId(campaignId);
                    }
                });
        }
    } else {
        message.channel.send("Campaign data must be an integer.");
    }
}

function getCampaigns() {
    return fetch("https://www.loot.senorpez.com/campaigns")
        .then(response => response.json());
}

function findCampaignById(campaignId) {
    return getCampaigns().then(data => {
        return data._embedded['loot-api:campaign'].filter(campaign => campaign.id === campaignId);
    });
}

function findCampaignByName(campaignName) {
    return getCampaigns().then(data => {
        return data._embedded['loot-api:campaign'].filter(campaign => campaign.name.toLowerCase().includes(campaignName.toLowerCase()));
    })
}