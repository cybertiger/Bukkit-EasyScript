/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript.config;

import java.util.ArrayList;
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
    private Map<String,String> systemProperties = new HashMap<String,String>();
    private List<String> libraries = new ArrayList<String>();
    {
        libraries.add("library");
    }
    private List<String> scriptPath = new ArrayList<String>();
    {
        scriptPath.add("scripts");
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
}
