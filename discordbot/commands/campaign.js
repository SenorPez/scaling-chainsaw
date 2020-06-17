require("dotenv").config();
const regex = /^.+?(?:\s)+(.+)/g;

const api = require('../service/api')
const state = require('../service/state');

function ParseError() {
    this.message = "Usage: $campaign <campaign name>";
}

function CampaignNotFoundError(campaignName) {
    this.message = `Campaign containing '${campaignName}' not found`;
}

function MultipleMatchError(campaignName, data) {
    this.message = `Multiple campaigns containing '${campaignName}' found:`;
    data.forEach(campaign => this.message = this.message + `\nID: ${campaign.id} Name: ${campaign.name}`);
}

function CampaignIdNotFoundError(campaignId) {
    this.message = `Campaign with ID of ${campaignId} not found`;
}

module.exports = (message) => {
    return module.exports.parseMessage(message)
        .then(matches => module.exports.parseArguments(matches))
        .then(searchParam => {
            return (typeof searchParam === 'number')
                ? module.exports.findCampaignById(searchParam)
                : module.exports.findCampaignByName(searchParam);
        })
        .then(campaign => module.exports.setCampaign(campaign, message))
        .catch(error => message.channel.send(error.message));
};

module.exports.parseMessage = (message) => {
    return new Promise(resolve => {
        const matches = [...message.content.matchAll(regex)];

        if (matches[0]) {
            resolve(matches[0]);
        }
        throw new ParseError();
    });
};

module.exports.parseArguments = (matches) => {
    return new Promise(resolve => {
        const searchParam = Number(matches[1]).valueOf();
        resolve(isNaN(searchParam) ? matches[1] : searchParam);
    });
};

module.exports.getCampaigns = () => {
    return api.get(process.env.API_URL)
        .then(response => response.json())
        .then(apiindex => api.get(apiindex._links['loot-api:campaigns'].href));
};

module.exports.findCampaignByName = (campaignName) => {
    return module.exports.getCampaigns()
        .then(response => response.json())
        .then(campaigns => {
            const embeddedCampaign = campaigns._embedded['loot-api:campaign'].filter(campaign => campaign.name.toLowerCase().includes(campaignName.toLowerCase()));
            if (embeddedCampaign.length === 1) {
                return api.get(embeddedCampaign.pop()._links.self.href);
            } else if (embeddedCampaign.length < 1) {
                throw new CampaignNotFoundError(campaignName);
            }
            throw new MultipleMatchError(campaignName, embeddedCampaign);
        });
};

module.exports.findCampaignById = (campaignId) => {
    return module.exports.getCampaigns()
        .then(response => response.json())
        .then(campaigns => {
            const embeddedCampaign = campaigns._embedded['loot-api:campaign'].filter(embeddedCampaign => embeddedCampaign.id === campaignId);
            if (embeddedCampaign.length === 1) {
                return api.get(embeddedCampaign.pop()._links.self.href);
            }
            throw new CampaignIdNotFoundError(campaignId);
        });
};

module.exports.setCampaign = (campaign, message) => {
    return campaign.json()
        .then(campaignData => {
            message.channel.send(`Campaign set to ${campaignData.name}`);
            if (state.getCampaignId() !== campaignData.id) {
                state.setCampaignId(campaignData.id);
                state.setCharacterId(null);
            }
        });
};
