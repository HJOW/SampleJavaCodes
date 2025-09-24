package org.duckdns.hjow.samples.util;

import java.io.Serializable;

public class LongCounter implements Serializable {
    private static final long serialVersionUID = 7997897396717506277L;
    protected long value;
    public LongCounter() {
        
    }
    public LongCounter(long val) {
        this();
        this.value = val;
    }
    public void add(long val) {
        this.value += val;
    }
    public long getValue() {
        return value;
    }
    public void setValue(long value) {
        this.value = value;
    }
}
