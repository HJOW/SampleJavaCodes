package org.duckdns.hjow.samples.colonyman.elements;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.duckdns.hjow.commons.json.JsonArray;
import org.duckdns.hjow.commons.json.JsonObject;

public class ColonyBackup {
    protected List<Colony> colonies = new Vector<Colony>();
    protected String name, description;
    protected long created = 0L;
    public ColonyBackup() { }
    
    public void fromJson(JsonObject json) {
        setName(json.get("name").toString());
        setDescription(json.get("desc").toString());
        
        JsonArray list = (JsonArray) json.get("list");
        colonies.clear();
        if(list != null) {
            for(Object o : list) {
                if(o instanceof String) o = JsonObject.parseJson(o.toString());
                if(o instanceof JsonObject) {
                    try {
                        Colony col = new Colony();
                        col.fromJson((JsonObject) o);
                        colonies.add(col);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("type", "ColonyGroup");
        json.put("name", getName());
        json.put("desc", getDescription());
        
        JsonArray list = new JsonArray();
        for(Colony c : getColonies()) { list.add(c.toJson()); }
        json.put("list", list);
        
        return json;
    }

    public List<Colony> getColonies() {
        return colonies;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setColonies(List<Colony> colonies) {
        this.colonies = colonies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
    
    public Date getCreatedDate() {
        return new Date(getCreated());
    }
    
    public void setCreated(Date created) {
        this.created = created.getTime();
    }
}
