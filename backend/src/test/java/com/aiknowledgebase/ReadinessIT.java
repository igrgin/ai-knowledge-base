package com.aiknowledgebase;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

/** Full Spring/package checks live in Failsafe; no application fake is involved. */
class ReadinessIT {
    private static final String IMAGE = "pgvector/pgvector:0.8.7-pg17@sha256:ac08538c6f8b9904c33c8224c5e5706dbe760aca29db1d096972b4052c22a75d";
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    @TempDir Path temporary;

    @Test
    void packagedApplicationPreservesReadinessAndDatabaseStateAcrossRestarts() throws Exception {
        assertThat(Runtime.version().feature()).isEqualTo(26);
        try (var database = new PostgreSQLContainer(DockerImageName.parse(IMAGE).asCompatibleSubstituteFor("postgres"))) {
            int databasePort = freePort();
            database.withUrlParam("connectTimeout", "2").withUrlParam("socketTimeout", "3");
            database.withCreateContainerCmdModifier(command -> command.getHostConfig().withPortBindings(
                    new PortBinding(Ports.Binding.bindIpAndPort("127.0.0.1", databasePort), new ExposedPort(5432))));
            database.start();
            try (var connection = database.createConnection("")) {
                connection.createStatement().execute("CREATE EXTENSION vector");
                connection.createStatement().execute("CREATE TABLE restart_probe(value text); INSERT INTO restart_probe VALUES ('retained')");
                var result = connection.createStatement().executeQuery("SELECT '[1,2,3]'::vector <-> '[1,2,4]'::vector");
                assertThat(result.next()).isTrue();
                assertThat(result.getDouble(1)).isEqualTo(1);
            }
            int port = freePort();
            Process application = null;
            try {
                application = start(database, port);
                awaitStatus(application, port, 200);
                var html = get(port, "/");
                assertThat(html.statusCode()).isEqualTo(200);
                assertThat(html.body()).contains("AI knowledge base");
                var script = Pattern.compile("src=\"([^\"]+\\.js)\"").matcher(html.body());
                assertThat(script.find()).as("packaged frontend script").isTrue();
                var asset = get(port, script.group(1));
                assertThat(asset.statusCode()).isEqualTo(200);
                assertThat(asset.body()).contains("/actuator/health/readiness");

                var docker = DockerClientFactory.instance().client();
                docker.stopContainerCmd(database.getContainerId()).exec();
                awaitStatus(application, port, 503);
                docker.startContainerCmd(database.getContainerId()).exec();
                awaitStatus(application, port, 200);
                docker.restartContainerCmd(database.getContainerId()).exec();
                awaitStatus(application, port, 200);
                try (var connection = database.createConnection("")) {
                    var result = connection.createStatement().executeQuery("SELECT value FROM restart_probe");
                    assertThat(result.next()).isTrue();
                    assertThat(result.getString(1)).isEqualTo("retained");
                }
                stop(application);
                application = start(database, port);
                awaitStatus(application, port, 200);
                assertThat(Files.readString(temporary.resolve("application.log")))
                        .contains("profile is active: \"lvl2test\"").contains("using Java 26.");
            } finally {
                stop(application);
            }
        }
    }

    private int freePort() throws Exception {
        try (var socket = new ServerSocket(0, 0, java.net.InetAddress.getLoopbackAddress())) {
            return socket.getLocalPort();
        }
    }

    private Process start(PostgreSQLContainer database, int port) throws Exception {
        return new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-jar", "target/backend-0.1.0-SNAPSHOT.jar", "--spring.profiles.active=lvl2test",
                "--spring.config.additional-location=" + Path.of("src/test/resources").toAbsolutePath().toUri(),
                "--server.port=" + port,
                "--spring.datasource.url=" + database.getJdbcUrl(),
                "--spring.datasource.username=" + database.getUsername(),
                "--spring.datasource.password=" + database.getPassword())
                .redirectErrorStream(true).redirectOutput(temporary.resolve("application.log").toFile()).start();
    }

    private HttpResponse<String> get(int port, String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(5)).build(), HttpResponse.BodyHandlers.ofString());
    }

    private void awaitStatus(Process application, int port, int expected) throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(45).toNanos();
        while (application.isAlive() && System.nanoTime() < deadline) {
            try {
                var response = get(port, "/actuator/health/readiness");
                if (response.statusCode() == expected) {
                    assertThat(response.body()).contains(expected == 200 ? "UP" : "DOWN");
                    return;
                }
            } catch (java.io.IOException ignored) {
                // Startup or the owned container's transition can briefly close connections.
            }
            Thread.sleep(250);
        }
        throw new AssertionError("Readiness did not become " + expected + ": "
                + Files.readString(temporary.resolve("application.log")));
    }

    private void stop(Process process) throws Exception {
        if (process == null) return;
        process.destroy();
        if (!process.waitFor(15, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue();
        }
    }
}
