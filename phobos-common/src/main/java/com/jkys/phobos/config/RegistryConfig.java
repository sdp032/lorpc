package com.jkys.phobos.config;

import com.jkys.phobos.util.AppEnv;

import java.io.File;

/**
 * Created by lo on 1/5/17.
 */
public class RegistryConfig {
    public static final String NAME = "_phobosRegistryConfig";
    private static final String AUTO_JKS = "app.jks";
    private static final String AUTO_JKS_PASSWORD = "123456";
    private static final String KEYSTORE_ENV_NAME = "APP_KEYSTORE";
    private static final String KEYSTORE_PWD_ENV_NAME = "APP_KEYSTORE_PASSWORD";
    private static final RegistryConfig instance = new RegistryConfig();

    private String endpoint;
    private String appName;

    private String keystorePath;
    private String keystorePassword;

    private String caCertPath;
    private String certPath;
    private String keyPath;

    public RegistryConfig() {
        AppEnv env = AppEnv.get();
        endpoint = env.getEndpoint();
        if (new File(AUTO_JKS).exists()) {
            keystorePath = AUTO_JKS;
            keystorePassword = AUTO_JKS_PASSWORD;
        } else {
            String envKeyStore = System.getenv().get(KEYSTORE_ENV_NAME);
            if (envKeyStore != null && !envKeyStore.equals("")) {
                keystorePath = envKeyStore;
                keystorePassword = AUTO_JKS_PASSWORD;
                String envKeyStorePwd = System.getenv().get(KEYSTORE_PWD_ENV_NAME);
                if (envKeyStorePwd != null && !envKeyStorePwd.equals("")) {
                    keystorePassword = envKeyStorePwd;
                }
            } else {
                caCertPath = env.getCaCertPath();
                certPath = env.getCertPath();
                keyPath = env.getKeyPath();
            }
        }
    }

    public static RegistryConfig getInstance() {
        return instance;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getCaCertPath() {
        return caCertPath;
    }

    public void setCaCertPath(String caCertPath) {
        this.caCertPath = caCertPath;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }
}
