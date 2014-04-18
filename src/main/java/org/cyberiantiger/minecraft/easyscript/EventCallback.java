/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript;

import org.bukkit.event.Event;

/**
 *
 * @author antony
 */
public interface EventCallback<T extends Event> {
    
    public void callback(T t);

}
