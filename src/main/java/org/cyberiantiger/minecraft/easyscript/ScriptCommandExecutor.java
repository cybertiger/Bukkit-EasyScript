/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript;

import java.util.logging.Level;
import javax.script.ScriptException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author antony
 */
class ScriptCommandExecutor implements CommandExecutor {
    private final EasyScript plugin;
    private final CommandCallback callback;

    public ScriptCommandExecutor(EasyScript plugin, CommandCallback callback) {
        this.plugin = plugin;
        this.callback = callback;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        try {
            return callback.callback(cs, string, strings);
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING, ex.getMessage());
            return false;
        }
        /*
        try {
            return Boolean.TRUE == plugin.invokeLibraryFunction(function, cs, string, strings);
        } catch (ScriptException ex) {
            plugin.getLogger().log(Level.WARNING, ex.getMessage());
        } catch (NoSuchMethodException ex) {
            plugin.getLogger().log(Level.WARNING, ex.getMessage());
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING, ex.getMessage());
        }
        return false;
        */
    }
    
}
