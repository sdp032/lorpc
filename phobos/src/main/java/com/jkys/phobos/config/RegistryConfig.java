package com.jkys.phobos.config;

import org.cryptacular.util.CertUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * Created by lo on 1/5/17.
 */
public class RegistryConfig {
    public static final String NAME = "_phobosRegistryConfig";
    private String endpoint;
    private String keystorePath;
    private String keystorePassword;
    private String appName;

    public RegistryConfig() {
        endpoint = "https://xbus.91jkys.com:4433";
        keystorePath = "app.ks";
        keystorePassword = "123456";
    }

    public RegistryConfig(RegistryConfig other) {
        endpoint = other.endpoint;
        keystorePath = other.keystorePath;
        keystorePassword = other.keystorePassword;
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
}
