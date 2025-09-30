package org.duckdns.hjow.samples.colonyman.elements;

import java.io.Serializable;

/** 정착지 시나리오 정보 */
public class ColonyInformation implements Serializable {
    private static final long serialVersionUID = -764866136990865870L;
    protected String name, description, title;
    protected Class<?> colonyClass;
    
    public ColonyInformation() {}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Class<?> getColonyClass() {
        return colonyClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setColonyClass(Class<?> colonyClass) {
        this.colonyClass = colonyClass;
    }
}
