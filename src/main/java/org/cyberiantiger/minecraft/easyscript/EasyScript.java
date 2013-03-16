package org.cyberiantiger.minecraft.easyscript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.cyberiantiger.minecraft.easyscript.unsafe.CommandRegistration;

public class EasyScript extends JavaPlugin {
    public static final String SERVER_CONFIG = "server.yml";
    public static final String WORLD_CONFIG_DIRECTORY = "world";
    public static final String PLAYER_CONFIG_DIRECTORY = "player";

    private ScriptEngine engine;
    private Invocable invocable;
    private Compilable compilable;
    private ScriptContext engineContext;
    private boolean autoreload;
    private Map<File, Long> libraries;
    private Map<String, ScriptHolder> scripts;
    private List<File> scriptDirectories;
    private Map<String, PluginCommand> scriptCommands;
    private Config serverConfig;
    private Map<String, Config> worldConfig = new HashMap<String, Config>();
    private Map<String, Config> playerConfig = new HashMap<String, Config>();
    private final List<ScriptEventExecutor> registeredEventExecutors = new ArrayList<ScriptEventExecutor>();

    public EasyScript() {
        this.libraries = new HashMap<File, Long>();
        this.scripts = new HashMap<String, ScriptHolder>();
        this.scriptDirectories = new ArrayList<File>();
        this.scriptCommands = new HashMap<String, PluginCommand>();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        serverConfig = new Config(this, new File(getDataFolder(), SERVER_CONFIG));
        enableEngine();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        disableEngine();
        serverConfig.save();
        serverConfig = null;
        for (Config c : worldConfig.values()) {
            c.save();
        }
        worldConfig.clear();
        for (Config c : playerConfig.values()) {
            c.save();
        }
        playerConfig.clear();
    }

    private void enableEngine() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(EasyScript.class.getClassLoader());
            FileConfiguration config = getConfig();
            ScriptEngineManager manager = new ScriptEngineManager(EasyScript.class.getClassLoader());
            this.autoreload = config.getBoolean("autoreload");
            this.engine = manager.getEngineByName(config.getString("language"));
            if (this.engine == null) {
                getLogger().severe("Script engine named: " + config.getString("language") + " not found, disabling plugin.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            } else {
                if (engine.getFactory() != null) {
                    getLogger().info("Loaded scripting engine: " + engine.getFactory().getEngineName() + " version: " + engine.getFactory().getEngineVersion());
                } else {
                    getLogger().info("Broken script engine, engine.getFactory() returned null, probably beanshell.");
                }
            }
            if ((this.engine instanceof Invocable)) {
                this.invocable = ((Invocable) this.engine);
            } else {
                getLogger().severe("Selected scripting engine does not implement javax.script.Invocable, disabling plugin.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            if ((this.engine instanceof Compilable)) {
                this.compilable = ((Compilable) this.engine);
            } else {
                getLogger().severe("Selected scripting engine does not implement javax.script.Compilable, disabling plugin.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            this.engineContext = this.engine.getContext();
            this.engineContext.setAttribute("plugin", this, 100);
            this.engineContext.setAttribute("server", getServer(), 100);
            this.engineContext.setAttribute("log", getLogger(), 100);
            this.engineContext.setWriter(new LogWriter(Level.INFO));
            this.engineContext.setErrorWriter(new LogWriter(Level.WARNING));
            for (String s : config.getStringList("libraries")) {
                boolean found = false;
                for (String suffix : engine.getFactory().getExtensions()) {
                    File library = new File(getDataFolder(), s + '.' + suffix);
                    if (library.isFile()) {
                        this.libraries.put(library, Long.valueOf(library.lastModified()));
                        this.engineContext.setAttribute(ScriptEngine.FILENAME, library.getPath(), ScriptContext.ENGINE_SCOPE);
                        try {
                            this.engine.eval(new FileReader(library));
                        } catch (ScriptException ex) {
                            getLogger().log(Level.WARNING, "Error in library: " + library + ":" + ex.getMessage());
                        } catch (FileNotFoundException ex) {
                            // Should never happen.
                            getLogger().log(Level.SEVERE, null, ex);
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    getLogger().warning("Failed to find library file : " + s);
                }
            }
            for (String s : config.getStringList("scripts")) {
                File scriptDirectory = new File(getDataFolder(), s);
                if (!scriptDirectory.isDirectory()) {
                    getLogger().warning("Script directory not found : " + scriptDirectory);
                }

                this.scriptDirectories.add(scriptDirectory);
            }
            try {
                CommandRegistration.updateHelp(this, getServer());
            } catch (UnsupportedOperationException e) {
                // Ignored.
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    private void disableEngine() {
        for (Listener i : registeredEventExecutors) {
            HandlerList.unregisterAll(i);
        }
        registeredEventExecutors.clear();
        try {
            CommandRegistration.unregisterPluginCommands(getServer(), new HashSet(scriptCommands.values()));
            CommandRegistration.updateHelp(this, getServer());
        } catch (UnsupportedOperationException e) {
            // Ignored
        }
        scriptCommands.clear();
        this.engine = null;
        this.invocable = null;
        this.compilable = null;
        this.engineContext = null;
        this.libraries.clear();
        this.scripts.clear();
        this.scriptDirectories.clear();
    }

    /**
     * Invoke a function in EasyScript's configured libraries.
     * 
     * This is intended for use by other plugins, that require script support
     * and do not wish to deal with the scripting API.
     * 
     * @param function name of the function
     * @param args arguments
     * @return value returned from script
     * @throws ScriptException when there is an error calling the function
     * @throws NoSuchMethodException  when the function does not exist
     */
    public Object invokeLibraryFunction(String function, Object... args) throws ScriptException, NoSuchMethodException {
        checkLibraries();
        return invocable.invokeFunction(function, args);
    }

    /**
     * Invoke a script from EasyScript's configured script directories.
     * 
     * @param script name of the script file (excluding extension)
     * @param params parameters to pass to the script via the ScriptContext.
     * @return result of executing the script.
     * @throws ScriptException
     * @throws NoSuchMethodException 
     */
    public Object invokeScript(String script, Map<String,Object> params) throws ScriptException, NoSuchMethodException {
        ScriptHolder holder = getScript(script);
        if (holder == null) {
            throw new NoSuchMethodException("Script: " + script + " not found");
        }
        ScriptContext context = new EasyScriptContext(this.engine, this.engineContext);
        context.setAttribute(ScriptEngine.FILENAME, holder.getSource().getPath(), EasyScriptContext.SCRIPT_SCOPE);
        for (Map.Entry<String,Object> e : params.entrySet()) {
            context.setAttribute(e.getKey(), e.getValue(), EasyScriptContext.SCRIPT_SCOPE);
        }
        return holder.getScript().eval(context);
    }

    /**
     * Reload all scripts.
     */
    public void reload() {
        disableEngine();
        reloadConfig();
        enableEngine();
    }

    /**
     * Register a new command.
     * @param cmd The name of the command.
     * @param function The function in a library file to call.
     * @return The new command.
     */
    public PluginCommand registerCommand(String cmd, String function) {
        try {
            final PluginCommand command = CommandRegistration.registerCommand(this, cmd);
            if (command != null) {
                scriptCommands.put(cmd, command);
                command.setExecutor(new ScriptCommandExecutor(this, function));
                return command;
            }
        } catch (UnsupportedOperationException ex) {
            getLogger().log(Level.WARNING, null, ex);
        }
        return null;
    }

    /**
     * Register an event handler.
     *
     * Uses EventPriority.NORMAL and ignores cancelled events.
     *
     * @param eventClass The event to register the handler for.
     * @param function The function to call to handle this event.
     */
    public void registerEvent(Class<? extends Event> eventClass, String function) {
        registerEvent(eventClass, EventPriority.NORMAL, function);
    }

    /**
     * Register an event handler.
     *
     * Ignores cancelled events.
     *
     * @param eventClass The event to register the handler for.
     * @param priority The priority of the event handler.
     * @param function The function to call to handle this event.
     */
    public void registerEvent(Class<? extends Event> eventClass, EventPriority priority, String function) {
        registerEvent(eventClass, priority, true, function);
    }

    /**
     * Register an event handler.
     *
     * @param eventClass The event to register the handler for.
     * @param priority The priority of the event handler.
     * @param ignoreCancelled Whether the handler should be passed cancelled events.
     * @param function The function to call to handle this event.
     */
    public void registerEvent(Class<? extends Event> eventClass, EventPriority priority, boolean ignoreCancelled, final String function) {
        ScriptEventExecutor executor = new ScriptEventExecutor(this, eventClass, function);
        registeredEventExecutors.add(executor);
        getServer().getPluginManager().registerEvent(eventClass, executor, priority, executor, this, ignoreCancelled);
    }

    public File getWorldConfigDirectory() {
        return new File(getDataFolder(), WORLD_CONFIG_DIRECTORY);
    }

    public File getPlayerConfigDirectory() {
        return new File(getDataFolder(), PLAYER_CONFIG_DIRECTORY);
    }

    public Config getServerConfig() {
        return serverConfig;
    }

    public Config getWorldConfig(World world) {
        return getWorldConfig(world.getName());
    }

    public Config getWorldConfig(String world) {
        Config config = worldConfig.get(world);
        if (config == null) {
            config = new Config(this, new File(getWorldConfigDirectory(), world + ".yml"));
            worldConfig.put(world, config);
        }
        return config;
    }

    public Config getPlayerConfig(Player player) {
        return getPlayerConfig(player.getName());
    }

    public Config getPlayerConfig(String player) {
        Config config = playerConfig.get(player);
        if (config == null) {
            config = new Config(this, new File(getPlayerConfigDirectory(), player + ".yml"));
            playerConfig.put(player, config);
        }
        return config;
    }


    private boolean checkLibraries() {
        if (!this.autoreload) {
            return true;
        }
        for (Map.Entry<File, Long> e : this.libraries.entrySet()) {
            if (((File) e.getKey()).lastModified() > ((Long) e.getValue()).longValue()) {
                reload();
                return false;
            }
        }
        return true;
    }

    private ScriptHolder getScript(String name) throws ScriptException {
        ScriptHolder cached = this.scripts.get(name);

        if (cached != null) {
            if (this.autoreload) {
                File source = cached.getSource();
                if ((source.isFile()) && (source.lastModified() <= cached.getLastModified().longValue())) {
                    return cached;
                }
                this.scripts.remove(name);
            } else {
                return cached;
            }
        }

        LOOP:
        for (File dir : this.scriptDirectories) {
            for (String suffix : engine.getFactory().getExtensions()) {
                File script = new File(dir, name + '.' + suffix);
                if (script.isFile()) {
                    try {
                        CompiledScript compiledScript = this.compilable.compile(new FileReader(script));
                        cached = new ScriptHolder(compiledScript, script, Long.valueOf(script.lastModified()));
                        this.scripts.put(name, cached);
                    } catch (FileNotFoundException ex) {
                        // Should never happen.
                        getLogger().log(Level.SEVERE, null, ex);
                    }
                    break LOOP;
                }
            }
        }
        return cached;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("script".equals(command.getName())) {
            if (args.length < 1) {
                return false;
            }
            String script = args[0];
            String[] shiftArgs = new String[args.length - 1];
            System.arraycopy(args, 1, shiftArgs, 0, shiftArgs.length);

            Map<String,Object> env = new HashMap<String,Object>();
            if ((sender instanceof BlockCommandSender)) {
                env.put("block", ((BlockCommandSender)sender).getBlock());
            } else {
                env.put("block", null);
            }
            if ((sender instanceof Player)) {
                env.put("player", sender);
            } else {
                env.put("player", null);
            }
            env.put("sender", sender);
            env.put("args", shiftArgs);
            try {
                invokeScript(script, env);
            } catch (ScriptException ex) {
                sender.sendMessage("Error in script " + script + " " + ex.getMessage());
            } catch (NoSuchMethodException ex) {
                sender.sendMessage("Error in script " + script + " " + ex.getMessage());
            } catch (RuntimeException ex) {
                sender.sendMessage("Error in script, see server console.");
                getLogger().log(Level.WARNING, "Error in script: " + script, ex);
            }
            return true;
        }
        if ("scriptreload".equals(command.getName())) {
            if (args.length != 0) {
                return false;
            }
            reload();
            sender.sendMessage("Scripts reloaded.");
            return true;
        }
        return false;
    }

    private class LogWriter extends Writer {

        StringBuilder line = new StringBuilder();
        private final Level level;

        public LogWriter(Level level) {
            this.level = level;
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            for (int i = off; i < len; i++) {
                if (cbuf[i] == '\n') {
                    flush();
                } else {
                    this.line.append(cbuf[i]);
                }
            }
        }

        public void flush()
                throws IOException {
            EasyScript.this.getLogger().log(this.level, this.line.toString());
            this.line.setLength(0);
        }

        public void close()
                throws IOException {
        }
    }

    private static final class ScriptHolder {

        private final CompiledScript script;
        private final Long lastModified;
        private final File source;

        public ScriptHolder(CompiledScript script, File source, Long lastModified) {
            this.script = script;
            this.lastModified = lastModified;
            this.source = source;
        }

        public Long getLastModified() {
            return this.lastModified;
        }

        public CompiledScript getScript() {
            return this.script;
        }

        public File getSource() {
            return this.source;
        }
    }
}
