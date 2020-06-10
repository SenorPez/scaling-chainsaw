const regex = /^.+(?:\s)+(\d+)/g
const fetch = require("node-fetch");

module.exports = (message, state) => {
    const matches = [...message.content.matchAll(regex)];
    if (matches[0]) {
        const campaignId = Number(matches[0][1]).valueOf();

        findCampaign(campaignId)
            .then(data => {
                if (data.length < 1) {
                    message.channel.send(`Campaign with id of ${campaignId} not found.`);
                } else if (data.length > 1) {
                    message.channel.send(`Seriously, how did you manage this?`);
                }
                message.channel.send(`Campaign set to ${data[0].name}`);
                state.campaignId = campaignId;
            });
    } else {
        message.channel.send("Campaign data must be an integer.");
    }
}

function getCampaigns() {
    return fetch("https://www.loot.senorpez.com/campaigns")
        .then(response => response.json());
}

function findCampaign(campaignId) {
    return getCampaigns().then(data => {
        return data._embedded['loot-api:campaign'].filter(campaign => campaign.id === campaignId);
    });
}
