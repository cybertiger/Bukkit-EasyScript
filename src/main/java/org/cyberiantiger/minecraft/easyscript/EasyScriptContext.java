/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.easyscript;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

/**
 *
 * @author antony
 */
final class EasyScriptContext implements ScriptContext {
    public static final int SCRIPT_SCOPE = 50;
    private static final List<Integer> SCOPES = new ArrayList<Integer>(3);
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
