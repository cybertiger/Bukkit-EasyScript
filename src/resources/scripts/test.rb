# sender is set to the caller
$sender.sendMessage("Hello from ruby");

# args is a java String[]
$sender.sendMessage("You sent #{$args.size} arguments")

# If the sender is a player, then player is set.
unless $player.nil?
    $player.sendMessage("You are a player!")
end

# If the sender is a command block, then block is set.
unless $block.nil?
    $server.broadcastMessage("CommandBlock at #{$block.x}, #{$block.y}, #{$block.z} called ruby")
end

# Functions and variables from libraries are available here.
libraryFunction()
