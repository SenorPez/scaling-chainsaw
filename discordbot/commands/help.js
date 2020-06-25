module.exports = (message) => {
    message.channel.send("Available commands:\n" +
        "$campaign - Select campaign by id or name\n" +
        "$character - Select character by id or name\n" +
        "$additem - Add item by id or name to selected character\n" +
        "$dropitem - Drop item by id or name from selected character");
}