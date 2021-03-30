Hooks.once("init", () => {
    console.log("Loot Link | Initializing")
    initSettings();
});

Hooks.on("ready", async () => {
    let players = game.settings.get("loot-link", "players");
    console.log("Loot Link | Dump!");
    console.log(players);
})

function initSettings() {
    game.settings.register("loot-link", "players", {
        name: "Players",
        scope: "world",
        config: false,
        type: Array,
        default: ["b2uD590Xh97J07ke"]
    })
}