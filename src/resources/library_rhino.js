importClass(org.bukkit.event.EventPriority)
importClass(org.bukkit.event.player.PlayerJoinEvent)

log.info("Hello from javascript");

plugin.registerEvent(
    PlayerJoinEvent,
    EventPriority.MONITOR,
    true,
    function(event) {
        server.broadcastMessage("Welcome " + event.player.displayName + "!")
    })

var commandScripttest =
    plugin.registerCommand(
        "scripttest",
        function(sender, command, args) {
            sender.sendMessage("Hello from javascript")
            return true
        })

commandScripttest.setUsage("/<command>")
commandScripttest.setDescription("A command written in javascript")

var libraryFunction = function() {
    server.broadcastMessage("A script called a library function")
}
