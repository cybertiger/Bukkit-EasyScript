// sender is set to the
sender.sendMessage("Hello from groovy");

// args is a java String[]
sender.sendMessage("You sent " + args.length + " arguments")

// If the sender is a player, then player is set.
if (player) {
    player.sendMessage("You are a player!")
}

// If the sender is a command block, then block is set.
if (block) {
    server.broadcastMessage("CommandBlock at " + block.x + ", " + block.y + ", " + block.z + " called groovy")
}

// Functions and variables from libraries are available here.
libraryFunction()
