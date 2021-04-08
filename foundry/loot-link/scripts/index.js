Hooks.once("init", () => {
    console.log("Loot Link | Initializing")
});

Hooks.on("createItem", data => {
    const apiData = {
        actorId: data.actor.id,
        itemData: data.data
    }

    console.log("Loot Link | Created item (Actor: " + apiData.actorId + " Item: " + apiData.itemData._id);

    const Url = 'http://localhost:9090/foundryitems/';
    fetch(Url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(apiData)
    }).then(output => {
        console.log(output);
    });
});

Hooks.on("deleteItem", data => {
    console.log(data);
    const apiData = {
        actorId: data.actor.id,
        itemData: data.data
    }
    console.log(apiData);

    console.log("Loot Link | Removed item (Actor: " + apiData.actorId + " Item: " + apiData.itemData._id);

    const Url = 'http://localhost:9090/foundryitems/';
    fetch(Url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(apiData)
    }).then(output => {
        console.log(output);
    });
})
