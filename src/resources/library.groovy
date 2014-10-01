import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.EventPriority

log.info("Hello from groovy")

plugin.registerEvent(
    PlayerJoinEvent.class,
    EventPriority.MONITOR,
    true, 
    { event ->
        server.broadcastMessage("Welcome " + event.player.displayName + "!");
    })

def libraryScripttest = plugin.registerCommand(
    "scripttest",
    { sender, command, args ->
        sender.sendMessage("Hello from groovy")
        return true
    })

libraryScripttest.usage = "/<command>"
libraryScripttest.description = "A command written in groovy"

void libraryFunction() {
    server.broadcastMessage("A script called a library function")
}

plugin.invokeLibraryFunction("libraryFunction")
