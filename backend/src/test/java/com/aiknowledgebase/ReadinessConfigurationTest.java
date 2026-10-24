package com.aiknowledgebase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.assertj.core.api.Assertions.assertThat;

class ReadinessConfigurationTest {
    @Test
    void unavailableDatabaseCannotReportReady() throws Exception {
        try (ConfigurableApplicationContext context = SpringApplication.run(KnowledgeBaseApplication.class,
                "--server.port=0", "--spring.datasource.url=jdbc:postgresql://127.0.0.1:1/unavailable?connectTimeout=1")) {
            int port = context.getEnvironment().getProperty("local.server.port", Integer.class);
            var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:" + port + "/actuator/health/readiness")).build(),
                    HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(503);
            assertThat(response.body()).contains("DOWN").doesNotContain("password");
        }
    }
}
