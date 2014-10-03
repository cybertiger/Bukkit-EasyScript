/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author antony
 */
public class ESConfig {
    private boolean autoreload = false;
    private String jarDirectory = "lib";
    private String language = "javascript";
    private boolean useScriptScope = true;
    private Map<ESVariable,String> variableNames = new HashMap<ESVariable,String>();
    private Map<String,String> systemProperties = new HashMap<String,String>();
    private List<String> libraries = new ArrayList<String>();
    {
        libraries.add("library");
    }
    private List<String> scriptPath = new ArrayList<String>();
    {
        scriptPath.add("scripts");
    }

    public void init() {
        Map<ESVariable,String> variableNames = new EnumMap<ESVariable,String>(ESVariable.class);
        for (ESVariable var : ESVariable.values()) {
            if (this.variableNames.containsKey(var)) {
                variableNames.put(var, this.variableNames.get(var));
            } else {
                variableNames.put(var, var.getDefaultName());
            }
        }
        this.variableNames = variableNames;
    }

    public boolean isAutoreload() {
        return autoreload;
    }

    public String getJarDirectory() {
        return jarDirectory;
    }

    public String getLanguage() {
        return language;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public List<String> getLibraries() {
        return libraries;
    }

    public List<String> getScriptPath() {
        return scriptPath;
    }

    public boolean isUseScriptScope() {
        return useScriptScope;
    }

    public Map<ESVariable, String> getVariableNames() {
        return variableNames;
    }
}
