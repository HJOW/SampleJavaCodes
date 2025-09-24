package org.duckdns.hjow.samples.scripts;

import java.io.Serializable;

import org.duckdns.hjow.commons.core.Disposeable;

public interface ScriptObject extends Serializable, Disposeable {
    public String getName();
}

