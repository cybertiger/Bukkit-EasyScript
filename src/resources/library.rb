# Workaround because variables pass in in the global scope cease to exist
# once the script has been executed.
$_server = $server
$_plugin = $plugin
$_log = $log

module BE
  include_package "org.bukkit.event"
  include_package "org.bukkit.event.block"
  include_package "org.bukkit.event.enchantment"
  include_package "org.bukkit.event.entity"
  include_package "org.bukkit.event.hanging"
  include_package "org.bukkit.event.inventory"
  include_package "org.bukkit.event.painting"
  include_package "org.bukkit.event.player"
  include_package "org.bukkit.event.server"
  include_package "org.bukkit.event.vehicle"
  include_package "org.bukkit.event.weather"
  include_package "org.bukkit.event.world"
end

$_log.info "Hello from ruby";

$_plugin.registerEvent(
    BE::PlayerJoinEvent.java_class,
    BE::EventPriority::MONITOR,
    true,
    Proc.new do |event|
        $_server.broadcastMessage("Welcome #{event.player.display_name} to the server!")
    end)

commandScripttest = $_plugin.registerCommand(
    "scripttest",
    Proc.new do |sender,command,args|
        sender.sendMessage("Hello from ruby")
        true
    end)

commandScripttest.setUsage("/<command>")
commandScripttest.setDescription("A command implemented in ruby")

def libraryFunction()
    $_server.broadcastMessage("A script called a library function")
end

$_plugin.invokeLibraryFunction("libraryFunction")
