package com.revitalize.admincontrol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notification.mail")
public class NotificationEmailProperties {

    private String from = "nao-responda@admincontrol.local";
    private String displayName = "Admin Control - Alertas";

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
