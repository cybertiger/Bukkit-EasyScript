/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript.unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.help.HelpMap;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author antony
 */
public final class CommandRegistration {

    public CommandRegistration() {
    }

    public static PluginCommand registerCommand(Plugin plugin, String command) {
        // Very Dirty Hack.
        try {
            Constructor<PluginCommand> cons = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            cons.setAccessible(true);
            PluginCommand ret = cons.newInstance(command, plugin);
            getCommandMap(plugin.getServer()).register(plugin.getName(), ret);
            return ret;
        } catch (NoSuchMethodException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (SecurityException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (InstantiationException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (InvocationTargetException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static void unregisterCommand(Server server, PluginCommand command) {
        try {
            SimpleCommandMap map = getCommandMap(server);
            command.unregister(map);
            // If only it was that easy, thankfully SimpleCommandMap leaks a
            // mutable reference to it's private hashmap.
            Iterator<Command> i = map.getCommands().iterator();
            while (i.hasNext()) {
                if (i.next() == command) {
                    i.remove();
                    break;
                }
            }
        } catch (NoSuchMethodException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (InvocationTargetException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static void updateHelp(Plugin plugin, Server server) {
        HelpMap help = server.getHelpMap();
        help.clear();
        try {
            Method initTopics = help.getClass().getMethod("initializeGeneralTopics");
            initTopics.invoke(help);
            Method initCommands = help.getClass().getMethod("initializeCommands");
            initCommands.invoke(help);
        } catch (NoSuchMethodException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (SecurityException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (InvocationTargetException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static void unregisterPluginCommands(Server server, Set<PluginCommand> commands) {
        try {
            SimpleCommandMap map = getCommandMap(server);
            Iterator<Map.Entry<String,Command>> i = getKnownCommands(map).entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String,Command> e = i.next();
                Command c = e.getValue();
                if (c instanceof PluginCommand) {
                    if (commands.contains(c)) {
                        c.unregister(map);
                        i.remove();
                    }
                }
            }
        } catch (NoSuchMethodException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (InvocationTargetException ex) {
            throw new UnsupportedOperationException(ex);
        } catch (NoSuchFieldException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    private static Map<String,Command> getKnownCommands(SimpleCommandMap map) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field knownCommands = map.getClass().getDeclaredField("knownCommands");
        knownCommands.setAccessible(true);
        return (Map<String, Command>) knownCommands.get(map);
    }

    private static SimpleCommandMap getCommandMap(Server server) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method getCommandMapMethod = server.getClass().getMethod("getCommandMap");
        return (SimpleCommandMap) getCommandMapMethod.invoke(server);
    }
}
