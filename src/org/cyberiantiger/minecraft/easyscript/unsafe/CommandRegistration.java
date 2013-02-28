/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.easyscript.unsafe;

import org.bukkit.Server;
import org.cyberiantiger.minecraft.easyscript.EasyScript.ScriptCommand;

/**
 *
 * @author antony
 */
public interface CommandRegistration {
    public void registerCommand(Server server, ScriptCommand command);
    public void unregisterCommand(Server server, ScriptCommand command);
}
