require("dotenv").config();
const regex = /^.+?(?:\s)+(.+)/g

const api = require('../service/api')
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
                        if (state.getCampaignId() !== data[0].id) {
                            state.setCharacterId(null);
                            state.setCampaignId(data[0].id);
                        }
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
                        if (state.getCampaignId() !== data[0].id) {
                            state.setCharacterId(null);
                            state.setCampaignId(data[0].id);
                        }
                    }
                });
        }
    } else {
        message.channel.send("Campaign data must be an integer.");
    }
}

module.exports.getCampaigns = () => {
    return api.get(process.env.API_URL)
        .then(response => response.json())
        .then(apiindex => api.get(apiindex._links['loot-api:campaigns'].href));
}

module.exports.findCampaignById = (campaignId) => {
    return module.exports.getCampaigns()
        .then(response => response.json())
        .then(campaigns => {
            console.log(campaigns);
            const embeddedCampaign = campaigns._embedded['loot-api:campaign'].filter(embeddedCampaign => embeddedCampaign.id === campaignId).pop();
            return api.get(embeddedCampaign._links.self.href);
        })
}

function findCampaignByName(campaignName) {
    return getCampaigns().then(data => {
        return data._embedded['loot-api:campaign'].filter(campaign => campaign.name.toLowerCase().includes(campaignName.toLowerCase()));
    })
}