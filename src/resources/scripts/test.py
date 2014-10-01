# sender is set to the script caller.
sender.sendMessage("Hello from python");

# args is a string array
sender.sendMessage("You sent " + str(len(args)) + " arguments")

# If the sender is a player, then player is set.
if player:
    player.sendMessage("You are a player!")

# If the sender is a command block, then block is set.
if block:
    server.broadcastMessage("CommandBlock at " + str(block.x) + ", " + str(block.y) + ", " + str(block.z) + " called python")

global libraryFunction

libraryFunction()
