package contracts;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import com.aiknowledgebase.KnowledgeBaseApplication;
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
    void incompatibleAcceptReturnsJson406WithProtocolHeaderAndServerCorrelation() throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            var negotiation = client.send(HttpRequest.newBuilder(
                            URI.create("http://127.0.0.1:" + port + "/api/http-contract/json"))
                    .header("Accept", "text/plain").header("X-Request-ID", "untrusted-client-value")
                    .GET().build(), HttpResponse.BodyHandlers.ofString());
            assertError(negotiation, 406, ErrorCode.RESPONSE_TYPE_NOT_ACCEPTABLE);
            assertEquals("application/json", negotiation.headers().firstValue("Accept").orElseThrow());
        }
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
    }
}
