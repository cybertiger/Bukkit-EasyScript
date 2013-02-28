/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.easyscript.unsafe;

import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
import org.cyberiantiger.minecraft.easyscript.EasyScript.ScriptCommand;

/**
 *
 * @author antony
 */
public class CommandRegistration147 implements CommandRegistration {

    public CommandRegistration147() throws ClassNotFoundException {
        Class.forName("org.bukkit.craftbukkit.v1_4_R1.CraftServer");
    }

    public void registerCommand(Server server, ScriptCommand command) {
        ((CraftServer)server).getCommandMap().register("script", command);
    }

    public void unregisterCommand(Server server, ScriptCommand command) {
        command.unregister(((CraftServer)server).getCommandMap());
    }

}
