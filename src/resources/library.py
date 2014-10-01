# Make server, plugin and log global.
global server
global plugin
global log

from org.bukkit.event import EventPriority
from org.bukkit.event.player import PlayerJoinEvent

log.info("Hello from python");

def onPlayerJoin(event):
    server.broadcastMessage("Welcome " + event.player.displayName +"!")

def onCommandScripttest(sender, command, args):
    sender.sendMessage("Hello from python");
    return True

plugin.registerEvent(
    PlayerJoinEvent,
    EventPriority.MONITOR,
    True,
    onPlayerJoin)

commandScripttest = plugin.registerCommand(
    "scripttest",
    onCommandScripttest)

commandScripttest.setUsage("/<command>")
commandScripttest.setDescription("A command written in python");

global libraryFunction

def libraryFunction():
    server.broadcastMessage("A script called a library function")

plugin.invokeLibraryFunction("libraryFunction")
