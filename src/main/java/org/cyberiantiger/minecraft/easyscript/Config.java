/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cyberiantiger.minecraft.easyscript;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Wrapper around a YAML configuration file.
 *
 * Note: when calling set(Object,Object) scripting language types may not
 * be correctly converted to what you meant.
 *
 * @author antony
 */
public class Config implements Configuration {
    private final File file;
    private YamlConfiguration config = new YamlConfiguration();
    private final EasyScript plugin;

    public Config(EasyScript plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    public void clear() {
        config = new YamlConfiguration();
    }

    public void load() {
        if (file.isFile()) {
            try {
                config.load(file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }
    }

    public void save() {
        File parentDir = file.getParentFile();
        if (!parentDir.isDirectory()) 
            parentDir.mkdir();
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public void reload() {
        clear();
        load();
    }

    public void addDefault(String string, Object o) {
        config.addDefault(string, o);
    }

    public void addDefaults(Map<String, Object> map) {
        config.addDefaults(map);
    }

    public void addDefaults(Configuration c) {
        config.addDefaults(c);
    }

    public void setDefaults(Configuration c) {
        config.setDefaults(c);
    }

    public Configuration getDefaults() {
        return config.getDefaults();
    }

    public ConfigurationOptions options() {
        return config.options();
    }

    public Set<String> getKeys(boolean bln) {
        return config.getKeys(bln);
    }

    public Map<String, Object> getValues(boolean bln) {
        return config.getValues(bln);
    }

    public boolean contains(String string) {
        return config.contains(string);
    }

    public boolean isSet(String string) {
        return config.isSet(string);
    }

    public String getCurrentPath() {
        return config.getCurrentPath();
    }

    public String getName() {
        return config.getName();
    }

    public Configuration getRoot() {
        return config.getRoot();
    }

    public ConfigurationSection getParent() {
        return config.getRoot();
    }

    public Object get(String string) {
        return config.get(string);
    }

    public Object get(String string, Object o) {
        return config.get(string, o);
    }

    public void set(String string, Object o) {
        config.set(string, o);
    }

    public ConfigurationSection createSection(String string) {
        return config.createSection(string);
    }

    public ConfigurationSection createSection(String string, Map<?, ?> map) {
        return config.createSection(string, map);
    }

    public String getString(String string) {
        return config.getString(string);
    }

    public String getString(String string, String string1) {
        return config.getString(string, string1);
    }

    public boolean isString(String string) {
        return config.isString(string);
    }

    public int getInt(String string) {
        return config.getInt(string);
    }

    public int getInt(String string, int i) {
        return config.getInt(string, i);
    }

    public boolean isInt(String string) {
        return config.isInt(string);
    }

    public boolean getBoolean(String string) {
        return config.getBoolean(string);
    }

    public boolean getBoolean(String string, boolean bln) {
        return config.getBoolean(string, bln);
    }

    public boolean isBoolean(String string) {
        return config.isBoolean(string);
    }

    public double getDouble(String string) {
        return config.getDouble(string);
    }

    public double getDouble(String string, double d) {
        return config.getDouble(string, d);
    }

    public boolean isDouble(String string) {
        return config.isDouble(string);
    }

    public long getLong(String string) {
        return config.getLong(string);
    }

    public long getLong(String string, long l) {
        return config.getLong(string, l);
    }

    public boolean isLong(String string) {
        return config.isLong(string);
    }

    public List<?> getList(String string) {
        return config.getList(string);
    }

    public List<?> getList(String string, List<?> list) {
        return config.getList(string, list);
    }

    public boolean isList(String string) {
        return config.isList(string);
    }

    public List<String> getStringList(String string) {
        return config.getStringList(string);
    }

    public List<Integer> getIntegerList(String string) {
        return config.getIntegerList(string);
    }

    public List<Boolean> getBooleanList(String string) {
        return config.getBooleanList(string);
    }

    public List<Double> getDoubleList(String string) {
        return config.getDoubleList(string);
    }

    public List<Float> getFloatList(String string) {
        return config.getFloatList(string);
    }

    public List<Long> getLongList(String string) {
        return config.getLongList(string);
    }

    public List<Byte> getByteList(String string) {
        return config.getByteList(string);
    }

    public List<Character> getCharacterList(String string) {
        return config.getCharacterList(string);
    }

    public List<Short> getShortList(String string) {
        return config.getShortList(string);
    }

    public List<Map<?, ?>> getMapList(String string) {
        return config.getMapList(string);
    }

    public Vector getVector(String string) {
        return config.getVector(string);
    }

    public Vector getVector(String string, Vector vector) {
        return config.getVector(string, vector);
    }

    public boolean isVector(String string) {
        return config.isVector(string);
    }

    public OfflinePlayer getOfflinePlayer(String string) {
        return config.getOfflinePlayer(string);
    }

    public OfflinePlayer getOfflinePlayer(String string, OfflinePlayer op) {
        return config.getOfflinePlayer(string, op);
    }

    public boolean isOfflinePlayer(String string) {
        return config.isOfflinePlayer(string);
    }

    public ItemStack getItemStack(String string) {
        return config.getItemStack(string);
    }

    public ItemStack getItemStack(String string, ItemStack is) {
        return config.getItemStack(string, is);
    }

    public boolean isItemStack(String string) {
        return config.isItemStack(string);
    }

    public Color getColor(String string) {
        return config.getColor(string);
    }

    public Color getColor(String string, Color color) {
        return config.getColor(string, color);
    }

    public boolean isColor(String string) {
        return config.isColor(string);
    }

    public ConfigurationSection getConfigurationSection(String string) {
        return config.getConfigurationSection(string);
    }

    public boolean isConfigurationSection(String string) {
        return config.isConfigurationSection(string);
    }

    public ConfigurationSection getDefaultSection() {
        return config.getDefaultSection();
    }
}
