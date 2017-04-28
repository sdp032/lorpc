package com.jkys.phobos.util;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XBusConfig;
import com.jkys.phobos.config.RegistryConfig;
import org.cryptacular.util.CertUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * Created by lo on 1/20/17.
 */
public class RegistryUtil {
    public static XBusClient getXBus(RegistryConfig cfg) {
        XBusConfig config;
        String[] endpoints = new String[]{cfg.getEndpoint()};
        if (cfg.getKeystorePath() != null) {
            config = new XBusConfig(endpoints, cfg.getKeystorePath(), cfg.getKeystorePassword());
        } else if (cfg.getKeyPath() != null) {
            config = new XBusConfig(endpoints, cfg.getCaCertPath(),
                    cfg.getCertPath(), cfg.getKeyPath());
        } else {
            config = new XBusConfig(endpoints, cfg.getAppName());
        }
        return new XBusClient(config);
    }

    public static String getAppName(RegistryConfig cfg) {
        String appName = null;
        if (cfg.getAppName() != null && cfg.getKeyPath() != null && cfg.getKeystorePassword() != null) {
            FileInputStream inputStream = null;
            try {
                KeyStore keyStore = KeyStore.getInstance("JKS");
                inputStream = new FileInputStream(cfg.getKeystorePath());
                keyStore.load(inputStream, cfg.getKeystorePassword().toCharArray());
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
            cfg.setAppName(appName);
        }
        return appName;
    }
}
