package com.aiknowledgebase;

import org.junit.jupiter.api.Test;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.assertj.core.api.Assertions.assertThat;

class ReadinessIT {
    private static final String IMAGE = "pgvector/pgvector:0.8.7-pg17@sha256:ac08538c6f8b9904c33c8224c5e5706dbe760aca29db1d096972b4052c22a75d";

    @Test
    void lvl2StartupChecksTheRealDatabaseAndReportsItsLoss() throws Exception {
        try (var database = new PostgreSQLContainer(DockerImageName.parse(IMAGE).asCompatibleSubstituteFor("postgres"))) {
            database.withCreateContainerCmdModifier(command -> command.getHostConfig().withPortBindings(
                    new PortBinding(Ports.Binding.bindIpAndPort("127.0.0.1", 0), new ExposedPort(5432))));
            database.start();
            try (var connection = database.createConnection("")) {
                connection.createStatement().execute("CREATE EXTENSION vector");
                var result = connection.createStatement().executeQuery("SELECT '[1,2,3]'::vector <-> '[1,2,4]'::vector");
                assertThat(result.next()).isTrue();
                assertThat(result.getDouble(1)).isEqualTo(1);
            }
            try (ConfigurableApplicationContext context = SpringApplication.run(KnowledgeBaseApplication.class,
                    "--spring.profiles.active=lvl2test", "--spring.datasource.url=" + database.getJdbcUrl() + "?connectTimeout=2&socketTimeout=3",
                    "--spring.datasource.username=" + database.getUsername(), "--spring.datasource.password=" + database.getPassword())) {
                assertThat(context.getEnvironment().getActiveProfiles()).containsExactly("lvl2test");
                int port = context.getEnvironment().getProperty("local.server.port", Integer.class);
                var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/actuator/health/readiness")).build();
                var client = HttpClient.newHttpClient();
                var ready = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertThat(ready.statusCode()).isEqualTo(200);
                assertThat(ready.body()).contains("UP");
                database.stop();
                var unavailable = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertThat(unavailable.statusCode()).isEqualTo(503);
                assertThat(unavailable.body()).contains("DOWN").doesNotContain(database.getPassword());
            }
        }
    }
}
