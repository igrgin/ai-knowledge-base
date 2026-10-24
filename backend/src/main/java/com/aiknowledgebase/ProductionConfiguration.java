package com.aiknowledgebase;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
@Profile("prod")
class ProductionConfiguration {
    ProductionConfiguration(Environment environment) {
        for (String key : new String[] {"SERVER_ADDRESS", "SERVER_PORT", "DATABASE_URL", "DATABASE_USER", "DATABASE_PASSWORD"}) {
            if (environment.getRequiredProperty(key).isBlank()) {
                throw new IllegalArgumentException("Production configuration requires a nonblank " + key);
            }
        }
    }
}
