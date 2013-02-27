package org.cyberiantiger.minecraft.easyscript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyScript extends JavaPlugin implements Listener {

    private ScriptEngine engine;
    private Invocable invocable;
    private Compilable compilable;
    private ScriptContext engineContext;
    private boolean autoreload;
    private String suffix;
    private Map<File, Long> libraries;
    private Map<String, ScriptHolder> scripts;
    private List<File> scriptDirectories;
    private Map<String, ScriptCommand> scriptCommands;

    public EasyScript() {
        this.libraries = new HashMap<File, Long>();
        this.scripts = new HashMap<String, ScriptHolder>();
        this.scriptDirectories = new ArrayList<File>();
        this.scriptCommands = new HashMap<String, ScriptCommand>();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        ScriptEngineManager manager = new ScriptEngineManager(EasyScript.class.getClassLoader());
        this.autoreload = config.getBoolean("autoreload");
        this.engine = manager.getEngineByName(config.getString("language"));
        if (this.engine == null) {
            getLogger().severe("Script engine named: " + config.getString("language") + " not found, disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.suffix = config.getString("suffix");
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
            File library = new File(getDataFolder(), s + this.suffix);
            if (library.isFile()) {
                this.libraries.put(library, Long.valueOf(library.lastModified()));
                try {
                    this.engine.eval(new FileReader(library));
                } catch (ScriptException ex) {
                    getLogger().log(Level.WARNING, "Error in library: " + library + ":" + ex.getMessage());
                } catch (FileNotFoundException ex) {
                    getLogger().log(Level.SEVERE, null, ex);
                }
            } else {
                getLogger().warning("Failed to find library file : " + library);
            }
        }
        for (String s : config.getStringList("scripts")) {
            File scriptDirectory = new File(getDataFolder(), s);
            if (!scriptDirectory.isDirectory()) {
                getLogger().warning("Script directory not found : " + scriptDirectory);
            }

            this.scriptDirectories.add(scriptDirectory);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.engine = null;
        this.invocable = null;
        this.compilable = null;
        this.engineContext = null;
        this.libraries.clear();
        this.scripts.clear();
        this.scriptDirectories.clear();
        CommandMap map = ((CraftServer)getServer()).getCommandMap();
        for (ScriptCommand command : scriptCommands.values()) {
            command.unregister(map);
        }
        scriptCommands.clear();
    }

    public void reload() {
        // Only way to unregister events.
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    public ScriptCommand registerCommand(String cmd, String function) {
        ScriptCommand command = new ScriptCommand(cmd, function);
        scriptCommands.put(cmd, command);
        CommandMap map = ((CraftServer)getServer()).getCommandMap();
        map.register("script", command);
        return command;
    }

    public void registerEvent(Class<? extends Event> eventClass, String function) {
        registerEvent(eventClass, EventPriority.NORMAL, function);
    }

    public void registerEvent(Class<? extends Event> eventClass, EventPriority priority, String function) {
        registerEvent(eventClass, priority, false, function);
    }

    public void registerEvent(Class<? extends Event> eventClass, EventPriority priority, boolean ignoreCancelled, final String function) {
        getServer().getPluginManager().registerEvent(eventClass, this, priority, new EventExecutor() {

            public void execute(Listener ll, Event event) throws EventException {
                if (!isEnabled()) {
                    return;
                }
                if (!checkLibraries()) {
                    return;
                }
                try {
                    EasyScript.this.invocable.invokeFunction(function, new Object[]{event});
                } catch (ScriptException ex) {
                    EasyScript.this.getLogger().log(Level.WARNING, "Error handling event: " + ex.getMessage());
                } catch (NoSuchMethodException ex) {
                    EasyScript.this.getLogger().log(Level.WARNING, "Library non-existent registered event handler function: " + function);
                }
            }
        }, this, ignoreCancelled);
    }

    private boolean checkLibraries() {
        if (!this.autoreload) {
            return true;
        }
        for (Map.Entry e : this.libraries.entrySet()) {
            if (((File) e.getKey()).lastModified() > ((Long) e.getValue()).longValue()) {
                reload();
                return false;
            }
        }
        return true;
    }

    private ScriptHolder getScript(String name) {
        ScriptHolder cached = (ScriptHolder) this.scripts.get(name);

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

        for (File dir : this.scriptDirectories) {
            File script = new File(dir, name + this.suffix);
            if (script.isFile()) {
                try {
                    CompiledScript compiledScript = this.compilable.compile(new FileReader(script));
                    cached = new ScriptHolder(compiledScript, script, Long.valueOf(script.lastModified()));
                    this.scripts.put(name, cached);
                } catch (FileNotFoundException ex) {
                    getLogger().log(Level.SEVERE, null, ex);
                } catch (ScriptException ex) {
                    getLogger().log(Level.WARNING, "Error in script:  " + script + " " + ex.getMessage());
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
            ScriptHolder holder = getScript(script);
            if (holder == null) {
                sender.sendMessage("Script not found: " + script);
                return true;
            }
            String[] shiftArgs = new String[args.length - 1];
            System.arraycopy(args, 1, shiftArgs, 0, shiftArgs.length);
            ScriptContext context = new EasyScriptContext(this.engine, this.engineContext);
            if ((sender instanceof BlockCommandSender)) {
                context.setAttribute("block", ((BlockCommandSender) sender).getBlock(), 50);
            } else {
                context.setAttribute("block", null, 50);
            }
            if ((sender instanceof Player)) {
                context.setAttribute("player", sender, 50);
            } else {
                context.setAttribute("player", null, 50);
            }
            context.setAttribute("args", shiftArgs, 50);
            try {
                holder.getScript().eval(context);
            } catch (ScriptException ex) {
                sender.sendMessage("Error in script " + holder.getSource() + " " + ex.getMessage());
            }
            return true;
        }
        if ("scriptreload".equals(command.getName())) {
            if (args.length != 0) {
                return false;
            }
            reload();
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

    private static final class EasyScriptContext
            implements ScriptContext {

        public static final int SCRIPT_SCOPE = 50;
        private static final List<Integer> SCOPES = new ArrayList(3);
        private final ScriptContext parent;
        private Bindings bindings;

        public EasyScriptContext(ScriptEngine engine, ScriptContext parent) {
            this.parent = parent;
            this.bindings = engine.createBindings();
        }

        public void setBindings(Bindings bindings, int scope) {
            if (scope == 50) {
                this.bindings = bindings;
            } else {
                this.parent.setBindings(bindings, scope);
            }
        }

        public Bindings getBindings(int scope) {
            if (scope == 50) {
                return this.bindings;
            }
            return this.parent.getBindings(scope);
        }

        public void setAttribute(String name, Object value, int scope) {
            if (scope == 50) {
                this.bindings.put(name, value);
            } else {
                this.parent.setAttribute(name, value, scope);
            }
        }

        public Object getAttribute(String name, int scope) {
            if (scope == 50) {
                return this.bindings.get(name);
            }
            return this.parent.getAttribute(name, scope);
        }

        public Object removeAttribute(String name, int scope) {
            if (scope == 50) {
                return this.bindings.remove(name);
            }
            return this.parent.removeAttribute(name, scope);
        }

        public Object getAttribute(String name) {
            return this.bindings.get(name);
        }

        public int getAttributesScope(String name) {
            if (this.bindings.containsKey(name)) {
                return 50;
            }
            return this.parent.getAttributesScope(name);
        }

        public Writer getWriter() {
            return this.parent.getWriter();
        }

        public Writer getErrorWriter() {
            return this.parent.getErrorWriter();
        }

        public void setWriter(Writer writer) {
            this.parent.setWriter(writer);
        }

        public void setErrorWriter(Writer writer) {
            this.parent.setErrorWriter(writer);
        }

        public Reader getReader() {
            return this.parent.getReader();
        }

        public void setReader(Reader reader) {
            this.parent.setReader(reader);
        }

        public List<Integer> getScopes() {
            return SCOPES;
        }

        static {
            SCOPES.add(Integer.valueOf(50));
            SCOPES.add(Integer.valueOf(100));
            SCOPES.add(Integer.valueOf(200));
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

    public final class ScriptCommand extends Command {

        private final String function;

        public ScriptCommand(String name, String function) {
            super(name);
            this.function = function;
        }

        public ScriptCommand(String name, String description, String usage, List<String> aliases, String function) {
            super(name, description, usage, aliases);
            this.function = function;
        }

        @Override
        public boolean execute(CommandSender cs, String string, String[] strings) {
            if (!isEnabled()) {
                return false;
            }
            if (!checkLibraries()) {
                return false;
            }
            if (function == null) {
                return false;
            }
            try {
                Object value = invocable.invokeFunction(function, cs, string, strings);
                if (Boolean.TRUE != value) {
                    cs.sendMessage(getUsage());
                }
                return true;
            } catch (ScriptException ex) {
                getLogger().log(Level.SEVERE, ex.getMessage());
            } catch (NoSuchMethodException ex) {
                getLogger().log(Level.SEVERE, ex.getMessage());
            }
            return false;
        }

        public String getFunction() {
            return function;
        }
    }
}
