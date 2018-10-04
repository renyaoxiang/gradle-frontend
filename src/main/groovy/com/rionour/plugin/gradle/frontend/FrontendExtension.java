package com.rionour.plugin.gradle.frontend;

public class FrontendExtension {

    String appName = "app";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        System.out.println("changed: " + appName);
        this.appName = appName;
    }
}
