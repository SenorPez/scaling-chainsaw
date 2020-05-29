require("dotenv").config()
const Discord = require("discord.js")
const fetch = require("node-fetch")
const client = new Discord.Client()

var campaignId = null
var characterId = null

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

function getToken() {
  const authData = {
    'grant_type': 'password',
    'client_id': 'api',
    'client_secret': process.env.CLIENT_SECRET,
    'username': 'senorpez',
    'password': process.env.PASSWORD
  };
  const authBody = Object.keys(authData).map(key => encodeURIComponent(key) + '=' + encodeURIComponent(authData[key])).join('&');

  return fetch(`https://www.senorpez.com:8448/auth/realms/loot/protocol/openid-connect/token`, {
    method: "post",
    body: authBody,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
  .then(response => response.json());
}

function processCommand(receivedMessage) {
    const regex = /(!\S*) (-?\d*) ?(.+?(?= --|$)) ?(.*)/g
    const matches = [...receivedMessage.content.matchAll(regex)]
    const command = matches[0][1]
    const quantity = matches[0][2]
    const item = matches[0][3]
    const args = matches[0][4]

    if (command == "!add" || command == "!drop" || command == "!setCharges") {
      if (campaignId === null || characterId === null) {
          receivedMessage.channel.send("Campaign or character ID not set. Set with !cid and !pid.")
          return
      }
    }

    if (command == "!add"){
        addItem(receivedMessage, quantity, item, args)
    }
    if (command == "!drop") {
        dropItem(receivedMessage, quantity, item, args)
    }
    if (command == "!cid") {
        // Number goes into the item capture group
        campaignId = item
        receivedMessage.channel.send(`Campaign ID set to ${campaignId}`)
    }
    if (command == "!pid") {
      // Number goes into the item capture group
        characterId = item
        receivedMessage.channel.send(`Character Id set to ${characterId}`)
    }
    if (command == "!setcharges") {
        setCharges(receivedMessage, quantity, item, args)
    }
}

function addItem(receivedMessage, quantity, item, args) {
    if (quantity == "") {
        quantity = 1
    }

    const itemPromise = getItems(receivedMessage, item, args);
    const tokenPromise = getToken();
  
    Promise.all([itemPromise, tokenPromise])
    .then((values) => {
      if (values[0] !== undefined) {
      addJsonItem(values[0], quantity, args, values[1].access_token);  
      }
    });
}

function dropItem(receivedMessage, quantity, item, args) {
    if (quantity == "") {
        quantity = -1
    } else {
        quantity = -quantity
    }

    addItem(receivedMessage, quantity, item, args);
}

function getItems(receivedMessage, item, args) {
  return fetch("https://www.loot.senorpez.com/items")
  .then(response => response.json())
  .then(data => getItemId(receivedMessage, item, args, data));
}

function getItemId(receivedMessage, item, args, data) {
  const itemArray = data._embedded['loot-api:lootitem']
  const filteredItemArray = itemArray.filter(singleItem => singleItem.name == item)

  const idRegEx = /(--[i]) ?(.+?(?=--|$))/g
  const idMatches = [...args.matchAll(idRegEx)]

  if (filteredItemArray.length < 1) {
    receivedMessage.channel.send("Item not found.");
  } else if (filteredItemArray.length > 1 && idMatches.length != 1) {
    receivedMessage.channel.send("Multiple item matches. Include item ID with --i option.")
  } else if (filteredItemArray.length > 1 && idMatches.length == 1) {
    return idMatches[0][2];
  } else {
    return filteredItemArray[0].id;
  }
}

function addJsonItem(itemId, quantity, args, access_token, autoremark) {
  const remarkRegEx = /(--[r]) ?(.+?(?=--|$))/g
  const remarkMatches = [...args.matchAll(remarkRegEx)]
  let remark = null

  if (autoremark === undefined) {
    autoremark = "";
  }
  
  if (remarkMatches.length > 0) {
    remark = [remarkMatches[0][2], autoremark].join(" ");
  } else if (autoremark !== "") {
    remark = autoremark;
  }
  
  let addItem = {
    item: itemId,
    quantity: quantity,
    remark: remark
  }

  const authHeader = `bearer ${access_token}`

  fetch(`https://www.loot.senorpez.com/campaigns/${campaignId}/characters/${characterId}/itemtransactions`, {
    method: "post",
    body: JSON.stringify(addItem),
    headers: {
      "Content-Type": "application/hal+json",
      "Authorization": authHeader,
    }
  })
  .then(response => response.json())
  .then(data => console.log(data));
}


function setCharges(receivedMessage, quantity, item, args) {
  const itemPromise = getItems(receivedMessage, item, args);
  const tokenPromise = getToken();

  Promise.all([itemPromise, tokenPromise])
  .then((values) => {
    if (values[0] !== undefined) {
    addJsonItem(values[0], 0, args, values[1].access_token, `Charges ${quantity}`);
    setItemCharges(values[0], quantity, values[1].access_token);
    }
  });
}

function setItemCharges(itemId, quantity, access_token) {
  const setCharges = {
    charges: quantity
  }

  const authHeader = `bearer ${access_token}`

  fetch(`https://www.loot.senorpez.com/items/${itemId}`, {
    method: "put",
    body: JSON.stringify(setCharges),
    headers: {
      "Content-Type": "application/hal+json",
      "Authorization": authHeader,
    }
  })
  .then(response => response.json())
  .then(data => console.log(data));
}

client.login(process.env.BOT_TOKEN)
