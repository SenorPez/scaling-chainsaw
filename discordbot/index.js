require("dotenv").config()
const Discord = require("discord.js")
const fetch = require("node-fetch")
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
  const regex = /(!\S*) (-?\d*) ?(.+?(?= --|$)) ?(.*)/g
  const matches = [...receivedMessage.content.matchAll(regex)]
  const command = matches[0][1]
  const quantity = matches[0][2]
  const item = matches[0][3]
  const args = matches[0][4]

  if (command == "!add") {
    addItem(receivedMessage, quantity, item, args)
  }
  if (command == "!drop") {
    dropItem(receivedMessage, quantity, item, args)
  }
}

function dropItem(receivedMessage, quantity, item, args) {
  if (quantity == "") {
    quantity = -1
  } else {
    quantity = -quantity
  }

  addItem(receivedMessage, quantity, item, args)
}

function addItem(receivedMessage, quantity, item, args) {
  if (quantity == "") {
    quantity = 1
  }

  jsonAddItem(receivedMessage, quantity, item, args)
}

function jsonAddItem(receivedMessage, quantity, item, args) {
  fetch("http://senorpez.com:9090/items")
    .then(response => {
      return response.json()
    })
    .then(data => {
      const itemArray = data._embedded['loottable-api:lootitem']
      const filteredItemArray = itemArray.filter(singleItem => singleItem.name == item)

      const idRegEx = /(--[i]) ?(.+?(?=--|$))/g
      const idMatches = [...args.matchAll(idRegEx)]

      if (filteredItemArray.length < 1) {
        receivedMessage.channel.send("Item not found.")
      } else if (filteredItemArray.length > 1 && idMatches.length < 1) {
        receivedMessage.channel.send("Multiple item matches. Include item ID with --i option.")
      } else if (filteredItemArray.length > 1 && idMatches.length >= 1) {
        const itemId = idMatches[0][2]
        const remarkRegEx = /(--[r]) ?(.+?(?=--|$))/g
        const remarkMatches = [...args.matchAll(remarkRegEx)]
        let remark = null
        if (remarkMatches.length > 0) {
          remark = remarkMatches[0][2]
        }

        let addItem = {item: itemId, quantity: quantity, remark: remark}

        fetch("http://senorpez.com:9090/campaigns/1/players/2/itemtransactions",
          {method: "post", body: JSON.stringify(addItem), headers: {
            "Content-Type": "application/hal+json"
          }})
          .then(function(response) {
            return response.json()
          })
          .then(data => {
            console.log(data)
          })

      } else {
        const itemId = filteredItemArray[0].id

        const remarkRegEx = /(--[r]) ?(.+?(?=--|$))/g
        const remarkMatches = [...args.matchAll(remarkRegEx)]
        let remark = null
        if (remarkMatches.length > 0) {
          remark = remarkMatches[0][2]
        }
        let addItem = {item: itemId, quantity: quantity, remark: remark}

        fetch("http://senorpez.com:9090/campaigns/1/players/2/itemtransactions",
          {method: "post", body: JSON.stringify(addItem), headers: {
            "Content-Type": "application/hal+json"
          }})
          .then(function(response) {
            return response.json()
          })
          .then(data => {
            console.log(data)
          })
      }
    })
}

client.login(process.env.BOT_TOKEN)

