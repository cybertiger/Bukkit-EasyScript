/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript;

import org.bukkit.command.CommandSender;

/**
 *
 * @author antony
 */
public interface CommandCallback {

    public boolean callback(CommandSender sender, String command, String[] args);
    
}
