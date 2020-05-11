const Discord = require("discord.js")
const client = new Discord.Client()

client.on("ready", () => {
  console.log(`Logged in as ${client.user.tag}`)
})

client.on("message", receivedMessage => {
  // Prevent bot from responding to its own messages
  if (receivedMessage.author == client.user) {
    return
  }

  if (receivedMessage.content.startsWith("!")) {
    processCommand(receivedMessage)
  }
})

function processCommand(receivedMessage) {
  let fullCommand = receivedMessage.content.substr(1)

  let regex = /(!\S+) (\d*)? ?(.+) -r[\s=]?(.+)/g
  let str = receivedMessage.content

  let matches = [...str.matchAll(regex)]
  let command = matches[0][1]
  let quantity = matches[0][2]
  let item = matches[0][3]
  let remark = matches[0][4]

  console.log(matches, command, quantity, item, remark)
}

bot_secret_token = "NjE4OTM2NTYxODMyMjk2NDQ5.XXA--g.EZlSHibXiiv-UgpC1ETj574L1mE"
client.login(bot_secret_token)

