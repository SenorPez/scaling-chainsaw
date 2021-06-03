const modId = "loot-link";
const modName = "Loot Link";

const Url = 'http://localhost:9090/foundryitems/';

async function addItem(wrapped, ...args) {
    args[1] = [args[1][0], args[1][0]];
    const actorId = this.id;

    for (const item of args[1]) {
        const apiData = {
            actorId: actorId,
            item: item
        };

        await fetch(Url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(apiData)
        })
            .then(response => {
                if (!response.ok) throw new Error("Fetch Error");
                return response.json();
            })
            .then(data => {
                console.log(modId + " | Created item with ID " + data.id);
                return data.id;
            })
            .then(id =>
                wrapped(args[0], [item])
                    .then(newItems => newItems.forEach(newItem => newItem.setFlag(modId, "id", id))))
            .catch(error => console.error("Error: " + error));
    }
}
//
// async function deleteItem(wrapped, ...args) {
//     const actor = args[1].parent;
//     const itemFoundryId = args[0][0];
//     const itemId = actor.getEmbeddedCollection("Item").get(itemFoundryId).getFlag(modId, "id");
//
//     console.log(itemId);
//
//     const apiData = {
//         id: itemId
//     };
//
//     await fetch(Url, {
//         method: 'DELETE',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify(apiData)
//     })
//         .then(response => {
//             if (!response.ok) throw new Error('Error recieved');
//             return response.json();
//         })
//         .then(data => {
//             console.log(data);
//             console.log(modId + " | Deleted item with ID " + data.id);
//             return data.id;
//         })
//         .then(id => console.log("Run wrapped"))
//         .catch(error => console.error("Error: " + error));
// }

Hooks.once("init", () => {
    console.log(modName + " | Initializing")
});

Hooks.once("ready", () => {
    if(!game.modules.get('lib-wrapper')?.active && game.user.isGM) {
        ui.notifications.error("Loot Link requires 'libwrapper'. Install and activate the module.");
        console.log(modName + " | libwrapper Not Found");
    }

    libWrapper.register(modId, 'Actor.prototype.createEmbeddedDocuments', addItem, 'MIXED');
    // libWrapper.register(modId, 'Item.deleteDocuments', deleteItem, 'MIXED');
})
