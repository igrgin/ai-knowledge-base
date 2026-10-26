package contracts;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import com.aiknowledgebase.KnowledgeBaseApplication;
import com.aiknowledgebase.shared.ApplicationException;
import com.aiknowledgebase.shared.ErrorCode;
import com.aiknowledgebase.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {KnowledgeBaseApplication.class, ErrorRepresentationITests.HttpConsumer.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ErrorRepresentationITests extends PostgresIntegrationTest {
    @LocalServerPort private int port;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        freshDatabase().registerProperties(registry);
    }

    @Test
    void restrictiveAcceptKeepsJsonErrorsAndProtocolHeadersWithoutChangingSuccessRepresentations() throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            String base = "http://127.0.0.1:" + port;
            var negotiation = send(client, base, "/api/http-contract/json", "GET", "text/plain");
            assertError(negotiation, 406, ErrorCode.RESPONSE_TYPE_NOT_ACCEPTABLE);
            assertEquals("application/json", negotiation.headers().firstValue("Accept").orElseThrow());

            assertError(send(client, base, "/api/http-contract/missing", "GET", "text/plain"),
                    404, ErrorCode.RESOURCE_NOT_FOUND);
            var method = send(client, base, "/api/http-contract/json", "POST", "text/plain");
            assertError(method, 405, ErrorCode.METHOD_NOT_ALLOWED);
            assertTrue(method.headers().firstValue("Allow").orElseThrow().contains("GET"));

            var json = send(client, base, "/api/http-contract/json", "GET", "application/json");
            assertEquals(200, json.statusCode());
            assertEquals("application/json", json.headers().firstValue("Content-Type").orElseThrow());
            assertEquals("ok", JsonMapper.builder().build().readTree(json.body()).path("status").asString());
            var text = send(client, base, "/api/http-contract/text", "GET", "text/plain");
            assertEquals(200, text.statusCode());
            assertTrue(text.headers().firstValue("Content-Type").orElseThrow().startsWith("text/plain"));
            assertEquals("ok", text.body());
        }
    }

    private HttpResponse<String> send(HttpClient client, String base, String path, String method, String accept)
            throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + path))
                .header("Accept", accept).header("X-Request-ID", "untrusted-client-value")
                .method(method, HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
    }

    private void assertError(HttpResponse<String> response, int status, ErrorCode code) throws Exception {
        assertEquals(status, response.statusCode());
        assertEquals("application/json", response.headers().firstValue("Content-Type").orElseThrow());
        var body = JsonMapper.builder().build().readTree(response.body());
        assertEquals(3, body.size());
        assertEquals(code.name(), body.path("code").asString());
        assertEquals(code.message(), body.path("message").asString());
        assertEquals(UUID.fromString(response.headers().firstValue("X-Request-ID").orElseThrow()),
                UUID.fromString(body.path("requestId").asString()));
    }

    // Registered explicitly in @SpringBootTest; this package is outside production component scanning.
    @RestController
    static class HttpConsumer {
        @GetMapping(value = "/api/http-contract/json", produces = MediaType.APPLICATION_JSON_VALUE)
        Map<String, String> json() {
            return Map.of("status", "ok");
        }

        @GetMapping(value = "/api/http-contract/text", produces = MediaType.TEXT_PLAIN_VALUE)
        String text() {
            return "ok";
        }

        @GetMapping("/api/http-contract/missing")
        void missing() {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
