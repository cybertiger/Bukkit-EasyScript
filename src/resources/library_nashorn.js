var EventPriority = Java.type('org.bukkit.event.EventPriority')
var PlayerJoinEvent = Java.type('org.bukkit.event.player.PlayerJoinEvent')
var EventCallback = Java.type('org.cyberiantiger.minecraft.easyscript.EventCallback')
var CommandCallback = Java.type('org.cyberiantiger.minecraft.easyscript.CommandCallback')

log.info("Hello from javascript")

plugin.registerEvent(
    PlayerJoinEvent.class,
    EventPriority.MONITOR,
    true,
    new EventCallback() { 
        callback: function(event) {
            server.broadcastMessage("Welcome " + event.player.displayName + "!")
        }
    })

var commandScripttest =
    plugin.registerCommand(
        "scripttest",
        new CommandCallback() {
            callback: function(sender, command, args) {
                sender.sendMessage("Hello from javascript")
                return true
            }
        })

commandScripttest.setUsage("/<command>")
commandScripttest.setDescription("A command written in javascript")

var libraryFunction = function() {
    server.broadcastMessage("A script called a library function")
}
