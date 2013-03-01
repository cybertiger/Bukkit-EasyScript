/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript.unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
import org.bukkit.craftbukkit.v1_4_R1.help.SimpleHelpMap;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public class CommandRegistration147 implements CommandRegistration {

    public CommandRegistration147() throws ClassNotFoundException {
        Class.forName("org.bukkit.craftbukkit.v1_4_R1.CraftServer");
    }

    public PluginCommand registerCommand(Plugin plugin, String command) {
        // Very Dirty Hack.
        try {
            Constructor<PluginCommand> cons = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            cons.setAccessible(true);
            PluginCommand ret = cons.newInstance(command, plugin);
            CommandMap map = ((CraftServer) plugin.getServer()).getCommandMap();
            map.register("easyscript", ret);
            return ret;
        } catch (NoSuchMethodException ex) {
            return null;
        } catch (SecurityException ex) {
            return null;
        } catch (InstantiationException ex) {
            return null;
        } catch (IllegalAccessException ex) {
            return null;
        } catch (IllegalArgumentException ex) {
            return null;
        } catch (InvocationTargetException ex) {
            return null;
        }
    }

    public void unregisterCommand(Server server, PluginCommand command) {
        SimpleCommandMap map = ((CraftServer) server).getCommandMap();
        command.unregister(map);
        // If only it was that easy, thankfully SimpleCommandMap leaks a
        // mutable reference to it's private hashmap.
        Iterator i = map.getCommands().iterator();
        while (i.hasNext()) {
            if (i.next() == command) {
                i.remove();
                break;
            }
        }
    }

    public void updateHelp(Server server) {
        ((SimpleHelpMap) server.getHelpMap()).clear();
        ((SimpleHelpMap) server.getHelpMap()).initializeGeneralTopics();
        ((SimpleHelpMap) server.getHelpMap()).initializeCommands();
    }

    public void unregisterPluginCommands(Server server, Plugin plugin) {
        SimpleCommandMap map = ((CraftServer) server).getCommandMap();
        Iterator<Command> i = map.getCommands().iterator();
        while (i.hasNext()) {
            Command c = i.next();
            if (c instanceof PluginCommand) {
                if (((PluginCommand)c).getPlugin() == plugin) {
                    c.unregister(map);
                    i.remove();
                }
            }
        }
    }
}
