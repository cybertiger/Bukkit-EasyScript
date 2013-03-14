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
    private final String function;
    private EasyScript plugin;

    public ScriptCommandExecutor(EasyScript plugin, String function) {
        this.plugin = plugin;
        this.function = function;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
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
    }
    
}
