/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.easyscript.unsafe;

import java.util.Collection;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public class DummyCommandRegistration implements CommandRegistration {

    public PluginCommand registerCommand(Plugin plugin, String command) {
        throw new UnsupportedOperationException("Command registration is only supported in CB1.4.7");
    }

    public void updateHelp(Server server) {
        throw new UnsupportedOperationException("Updating help is only supported in CD1.4.7");
    }

    public void unregisterPluginCommands(Server server, Plugin plugin) {
        throw new UnsupportedOperationException("Command registration is only supported in CB1.4.7");
    }
}
