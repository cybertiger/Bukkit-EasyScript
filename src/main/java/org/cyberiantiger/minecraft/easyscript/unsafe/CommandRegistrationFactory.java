/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.easyscript.unsafe;

/**
 *
 * @author antony
 */
public final class CommandRegistrationFactory {

    private CommandRegistrationFactory() {}

    public static CommandRegistration createCommandRegistration() {
        try {
            return new CommandRegistration147();
        } catch (Exception e) {
        } catch (Error e) {
        }
        return new DummyCommandRegistration();
    }
}
