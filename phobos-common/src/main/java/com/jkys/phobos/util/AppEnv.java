package com.jkys.phobos.util;

import java.util.Map;

/**
 * Created by lo on 1/7/17.
 */
public class AppEnv {
    public static final String UNKNOWN_APP = "unknown";
    private static final String DEFAULT_ENDPOINT = "xbus.qa.91jkys.com:4433";

    private String name;
    private String endpoint;
    private String caCertPath;
    private String certPath;
    private String keyPath;

    public static AppEnv get() {
        AppEnv appEnv = new AppEnv();
        Map<String, String> env = System.getenv();
        appEnv.name = env.get("APP_NAME");
        if (appEnv.name == null) {
            appEnv.name = System.getProperty("app.name");
        }
        if (appEnv.name == null) {
            appEnv.name = UNKNOWN_APP;
        }
        appEnv.endpoint = env.get("APP_ENDPOINT");
        if (appEnv.endpoint == null) {
            appEnv.endpoint = System.getProperty("app.endpoint");
        }
        if (appEnv.endpoint == null) {
            appEnv.endpoint = DEFAULT_ENDPOINT;
        }
        appEnv.caCertPath = env.get("APP_CACERT");
        if (appEnv.caCertPath == null) {
            appEnv.caCertPath = System.getProperty("app.cacert");
        }
        appEnv.certPath = env.get("APP_CERT");
        if (appEnv.certPath == null) {
            appEnv.certPath = System.getProperty("app.cert");
        }
        appEnv.keyPath = env.get("APP_KEY");
        if (appEnv.keyPath == null) {
            appEnv.keyPath = System.getProperty("app.key");
        }
        return appEnv;
    }

    public String getName () {
        return name;
    }

    public String getCaCertPath() {
        return caCertPath;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
