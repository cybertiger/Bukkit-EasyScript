/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript.unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
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
            Constructor<PluginCommand> cons =  PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            cons.setAccessible(true);
            PluginCommand ret = cons.newInstance(command, plugin);
            ((CraftServer)plugin.getServer()).getCommandMap().register("script", ret);
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
        command.unregister(((CraftServer) server).getCommandMap());
    }

    public void updateHelp(Server server) {
        ((SimpleHelpMap)server.getHelpMap()).clear();
        ((SimpleHelpMap)server.getHelpMap()).initializeGeneralTopics();
        ((SimpleHelpMap)server.getHelpMap()).initializeCommands();
    }


}
