const modId = "loot-link";
const modName = "Loot Link";

const Url = 'http://localhost:9090/foundryitems/';

async function logItem(wrapped, ...args) {
    const items = args[0];
    const actor = args[1].parent;

    const apiData = {
        actorId: actor.id,
        item: items[0]
    }

    await fetch(Url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(apiData)
    })
        .then(response => response.json())
        .then(data => {
            console.log(modId + " | Created item with ID " + data.id);
            return data.id;
        })
        .then(id => {
            wrapped(...args)
                .then(newItems => newItems.forEach(item => item.setFlag(modId, "id", id)));
        })
        .catch(error => {
            console.error("Error: " + error);
        });
}

Hooks.once("init", () => {
    console.log(modName + " | Initializing")
});

Hooks.once("ready", () => {
    if(!game.modules.get('lib-wrapper')?.active && game.user.isGM) {
        ui.notifications.error("Loot Link requires 'libwrapper'. Install and activate the module.");
        console.log(modName + " | libwrapper Not Found");
    }

    libWrapper.register(modId, 'Item.createDocuments', logItem, 'MIXED');
})
