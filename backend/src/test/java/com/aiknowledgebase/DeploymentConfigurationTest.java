package com.aiknowledgebase;

import java.util.Map;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import static org.assertj.core.api.Assertions.*;

class DeploymentConfigurationTest {
    record Database(String url, String username, String password) {}
    record Server(String address, int port) {}

    private StandardEnvironment configuration(String profile, Map<String, Object> supplied) {
        var environment = new StandardEnvironment();
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        environment.setActiveProfiles(profile);
        environment.getPropertySources().addFirst(new MapPropertySource("supplied-environment", supplied));
        ConfigDataEnvironmentPostProcessor.applyTo(environment);
        return environment;
    }

    @Test
    void basicSuiteUsesTheTestProfileWithoutStartingAnApplication() {
        assertThat(System.getProperty("spring.profiles.active")).isEqualTo("test");
        var environment = configuration("test", Map.of());
        assertThat(environment.getProperty("spring.main.web-application-type")).isEqualTo("none");
        assertThat(environment.getProperty("spring.datasource.url")).isNull();
        assertThat(environment.getProperty("spring.datasource.password")).isNull();
    }

    @Test
    void productionCannotBindWithoutEveryDeploymentValue() {
        var complete = Map.<String, Object>of("SERVER_ADDRESS", "127.0.0.1", "SERVER_PORT", "19080",
                "DATABASE_URL", "jdbc:postgresql://db.example:5432/deployment", "DATABASE_USER", "deployment-user",
                "DATABASE_PASSWORD", "supplied-secret");
        for (String missing : complete.keySet()) {
            var values = new java.util.HashMap<>(complete);
            values.remove(missing);
            new ApplicationContextRunner().withUserConfiguration(ProductionConfiguration.class)
                    .withInitializer(context -> context.setEnvironment(configuration("prod", values)))
                    .run(context -> assertThat(context).as("missing %s must fail deployment configuration", missing).hasFailed());
        }
    }

    @Test
    void suppliedProductionValuesBindWithoutLocalSettings() {
        var environment = configuration("prod", Map.of("SERVER_ADDRESS", "192.0.2.10", "SERVER_PORT", "19080",
                "DATABASE_URL", "jdbc:postgresql://db.example:5432/deployment", "DATABASE_USER", "deployment-user",
                "DATABASE_PASSWORD", "supplied-secret"));
        var binder = Binder.get(environment);
        assertThat(binder.bind("server", Bindable.of(Server.class)).get()).isEqualTo(new Server("192.0.2.10", 19080));
        assertThat(binder.bind("spring.datasource", Bindable.of(Database.class)).get()).isEqualTo(
                new Database("jdbc:postgresql://db.example:5432/deployment", "deployment-user", "supplied-secret"));
        assertThat(environment.getActiveProfiles()).containsExactly("prod");
        new ApplicationContextRunner().withUserConfiguration(ProductionConfiguration.class)
                .withInitializer(context -> context.setEnvironment(environment))
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void baseDoesNotAutomaticallySelectDevelopmentOrSupplyCredentials() {
        var environment = configuration("default", Map.of());
        assertThat(environment.getProperty("spring.profiles.default")).isNull();
        assertThat(environment.getProperty("spring.datasource.url")).isNull();
        assertThat(environment.getProperty("spring.datasource.username")).isNull();
        assertThat(environment.getProperty("spring.datasource.password")).isNull();
    }
}
