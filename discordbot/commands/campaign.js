require("dotenv").config();
const regex = /^.+?(?:\s)+(.+)/g

const api = require('../service/api')
const state = require('../service/state');

function ParseError() {
    this.message = "Usage: $campaign <campaign name>";
}

function CampaignNotFoundError(campaignName) {
    this.campaignName = campaignName;
}

function MultipleMatchError(data) {
    this.data = data;
}

function CampaignIdNotFoundError(campaignId) {
    this.message = `Campaign with ID of ${campaignId} not found`;
}

// module.exports = (message) => {
//     const matches = [...message.content.matchAll(regex)];
//     if (matches[0]) {
//         const campaignId = Number(matches[0][1]).valueOf();
//         if (isNaN(campaignId)) {
//             findCampaignByName(matches[0][1])
//                 .then(data => {
//                     if (data.length < 1) {
//                         message.channel.send(`Campaign with name of ${matches[0][1]} not found.`);
//                     } else if (data.length > 1) {
//                         message.channel.send(`Multiple matches`);
//                     } else {
//                         message.channel.send(`Campaign set to ${data[0].name}`);
//                         if (state.getCampaignId() !== data[0].id) {
//                             state.setCharacterId(null);
//                             state.setCampaignId(data[0].id);
//                         }
//                     }
//                 });
//         } else {
//             findCampaignById(campaignId)
//                 .then(data => {
//                     if (data.length < 1) {
//                         message.channel.send(`Campaign with id of ${campaignId} not found.`);
//                     } else if (data.length > 1) {
//                         message.channel.send(`Seriously, how did you manage this?`);
//                     } else {
//                         message.channel.send(`Campaign set to ${data[0].name}`);
//                         if (state.getCampaignId() !== data[0].id) {
//                             state.setCharacterId(null);
//                             state.setCampaignId(data[0].id);
//                         }
//                     }
//                 });
//         }
//     } else {
//         message.channel.send("Campaign data must be an integer.");
//     }
// }

module.exports.parseMessage = (message) => {
    return new Promise(resolve => {
        const matches = [...message.content.matchAll(regex)];

        if (matches[0]) {
            resolve(matches[0]);
        }
        throw new ParseError();
    })
}

module.exports.parseArguments = (matches) => {
    return new Promise((resolve, reject) => {
        /*
        Resolve: Text search for campaign.
        Reject: Lookup campaign by id.
         */
        const campaignId = Number(matches[1]).valueOf();
        isNaN(campaignId) ? resolve(matches[1]) : reject(campaignId);
    });
}

module.exports.getCampaigns = () => {
    return api.get(process.env.API_URL)
        .then(response => response.json())
        .then(apiindex => api.get(apiindex._links['loot-api:campaigns'].href));
}

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
            throw new MultipleMatchError(embeddedCampaign);
        })
}

module.exports.findCampaignById = (campaignId) => {
    return module.exports.getCampaigns()
        .then(response => response.json())
        .then(campaigns => {
            const embeddedCampaign = campaigns._embedded['loot-api:campaign'].filter(embeddedCampaign => embeddedCampaign.id === campaignId);
            if (embeddedCampaign.length === 1) {
                return api.get(embeddedCampaign.pop()._links.self.href);
            }
            throw new CampaignIdNotFoundError(campaignId);
        })
}
