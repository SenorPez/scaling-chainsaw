const regex = /^.+(?:\s)+(\d+)/g

module.exports = (message, state) => {
    const matches = [...message.content.matchAll(regex)];
    if (matches[0]) {
        const campaignId = Number(matches[0][1]).valueOf();
        state.campaignId = campaignId;
        message.channel.send(`Campaign set to ${campaignId}`);
    } else {
        message.channel.send("Campaign data must be an integer.");
    }
}