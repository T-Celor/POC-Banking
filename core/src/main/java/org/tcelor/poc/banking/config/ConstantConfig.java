package org.tcelor.poc.banking.config;

import org.eclipse.microprofile.config.ConfigProvider;

public class ConstantConfig {

    public static long TIMEOUT = ConfigProvider.getConfig()
            .getOptionalValue("banking.request.timeout", Long.class)
            .orElse(5000L);;

}
