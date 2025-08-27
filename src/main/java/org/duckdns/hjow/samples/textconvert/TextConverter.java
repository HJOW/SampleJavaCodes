package org.duckdns.hjow.samples.textconvert;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class TextConverter implements Serializable {
    private static final long serialVersionUID = -4042383080255543113L;

    public abstract String getName();
    public abstract String getDescription();
    public abstract String convert(String original, Map<String, String> parameters);
    
    public String convert(String original) {
        return convert(original, new HashMap<String, String>());
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
