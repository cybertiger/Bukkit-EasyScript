/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript.config;

/**
 *
 * @author antony
 */
public enum ESVariable {
    SERVER("server"),
    PLUGIN("plugin"),
    LOG("log"),
    SENDER("sender"),
    PLAYER("player"),
    BLOCK("block"),
    ARGS("args");
    private String defaultName;

    private ESVariable(String defaultName) {
        this.defaultName= defaultName;
    }

    public String getDefaultName() {
        return defaultName;
    }
}
