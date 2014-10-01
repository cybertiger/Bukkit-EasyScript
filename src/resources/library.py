# Workaround, this variables vanish after the script is initially
# executed, so we need to store them in our own global variables.
global _server
global _plugin
global _log
_server = server
_plugin = plugin
_log = log

from org.bukkit.event import EventPriority
from org.bukkit.event.player import PlayerJoinEvent

log.info("Hello from python");

def onPlayerJoin(event):
    _server.broadcastMessage("Welcome " + event.player.displayName +"!")

def onCommandScripttest(sender, command, args):
    sender.sendMessage("Hello from python");
    return True

_plugin.registerEvent(
    PlayerJoinEvent,
    EventPriority.MONITOR,
    True,
    onPlayerJoin)

commandScripttest = _plugin.registerCommand(
    "scripttest",
    onCommandScripttest)

commandScripttest.setUsage("/<command>")
commandScripttest.setDescription("A command written in python");
