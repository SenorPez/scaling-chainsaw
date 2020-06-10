const state = {
    campaignId: 1,
    characterId: 1
};

const getCampaignId = () => {
    return state.campaignId;
}

const setCampaignId = (campaignId) => {
    state.campaignId = campaignId;
}
const getCharacterId = () => {
    return state.characterId;
}

const setCharacterId = (characterId) => {
    state.characterId = characterId;
}

exports.getCampaignId = getCampaignId;
exports.setCampaignId = setCampaignId;
exports.getCharacterId = getCharacterId;
exports.setCharacterId = setCharacterId;