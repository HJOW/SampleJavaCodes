package org.duckdns.hjow.samples.scripts;

import java.io.Serializable;

import org.duckdns.hjow.samples.interfaces.Disposeable;

public interface ScriptObject extends Serializable, Disposeable {
    public String getName();
}

