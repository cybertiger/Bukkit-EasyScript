/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript;

import java.util.logging.Level;
import javax.script.ScriptException;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/**
 *
 * @author antony
 */
final class ScriptEventExecutor implements EventExecutor, Listener {
    private final EasyScript plugin;
    private final String function;
    private final Class<? extends Event> eventType;

    public ScriptEventExecutor(EasyScript plugin, Class<? extends Event> eventType, String function) {
        this.plugin = plugin;
        this.eventType = eventType;
        this.function = function;
    }

    public void execute(Listener l, Event event) throws EventException {
        try {
            plugin.invokeLibraryFunction(function, event);
        } catch (ScriptException ex) {
            plugin.getLogger().log(Level.WARNING, null, ex);
        } catch (NoSuchMethodException ex) {
            plugin.getLogger().log(Level.WARNING, null, ex);
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING, null, ex);
        }
    }
    
}
