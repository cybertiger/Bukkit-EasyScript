/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.easyscript.unsafe;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public interface CommandRegistration {
    public PluginCommand registerCommand(Plugin plugin, String command);
    public void unregisterCommand(Server server, PluginCommand command);
    public void updateHelp(Server server);
}
