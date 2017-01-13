package com.jkys.phobos.config;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XBusConfig;
import com.jkys.phobos.util.AppEnv;
import org.cryptacular.util.CertUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * Created by lo on 1/5/17.
 */
public class RegistryConfig {
    public static final String NAME = "_phobosRegistryConfig";
    public static final String AUTO_JKS = "app.jks";
    public static final String AUTO_JKS_PASSWORD = "123456";
    public static final String KEYSTORE_ENV_NAME = "APP_KEYSTORE";
    public static final String KEYSTORE_PWD_ENV_NAME = "APP_KEYSTORE_PASSWORD";

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

    public RegistryConfig(RegistryConfig other) {
        endpoint = other.endpoint;
        keystorePath = other.keystorePath;
        keystorePassword = other.keystorePassword;
    }

    public XBusClient getXBus() {
        XBusConfig config;
        if (keystorePath != null) {
            config = new XBusConfig(new String[]{endpoint}, keystorePath, keystorePassword);
        } else if (keyPath != null) {
            config = new XBusConfig(new String[]{endpoint}, caCertPath, certPath, keyPath);
        } else {
            throw new RuntimeException("missing key config");
        }
        return new XBusClient(config);
    }

    public String getAppName() {
        if (appName != null && keystorePath != null && keystorePassword != null) {
            FileInputStream inputStream = null;
            try {
                KeyStore keyStore = KeyStore.getInstance("JKS");
                inputStream = new FileInputStream(this.keystorePath);
                keyStore.load(inputStream, keystorePassword.toCharArray());
                appName = CertUtil.subjectCN((X509Certificate) keyStore.getCertificate("1"));
            } catch (Exception e) {
                // TODO error process
                e.printStackTrace();
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return appName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }
}
