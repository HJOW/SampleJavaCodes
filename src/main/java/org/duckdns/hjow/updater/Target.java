package org.duckdns.hjow.updater;

public interface Target {
	public String getSubType();
    public String getTitle();
    public String getMainUrl();
    public String getConfigUrl();
    public String getInstallPath();
    public String getFileName();
}
