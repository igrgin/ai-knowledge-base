package contracts;

import java.util.UUID;

import com.aiknowledgebase.shared.ApplicationException;
import com.aiknowledgebase.shared.ErrorCode;
import com.aiknowledgebase.shared.web.ApiExceptionHandler;
import com.aiknowledgebase.shared.web.RequestIdFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApiContractTest {
    private final JsonMapper json = JsonMapper.builder().build();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ContractConsumer())
                .setControllerAdvice(new ApiExceptionHandler())
                .addFilters(new RequestIdFilter())
                .build();
    }

    @Test
    void unexpectedFailureDoesNotDiscloseSqlCredentialsOrPrivateContent() throws Exception {
        UUID clientCorrelationId = UUID.randomUUID();
        MvcResult first = mvc.perform(get("/api/contract/failure").accept(MediaType.TEXT_PLAIN)
                        .header(RequestIdFilter.HEADER, clientCorrelationId.toString()))
                .andExpect(status().isInternalServerError()).andReturn();
        MvcResult second = mvc.perform(get("/api/contract/failure").accept(MediaType.TEXT_PLAIN)
                        .header(RequestIdFilter.HEADER, clientCorrelationId.toString()))
                .andExpect(status().isInternalServerError()).andReturn();
        assertError(first, "INTERNAL_ERROR", ErrorCode.INTERNAL_ERROR.message());
        assertError(second, "INTERNAL_ERROR", ErrorCode.INTERNAL_ERROR.message());
        UUID firstCorrelationId = UUID.fromString(first.getResponse().getHeader(RequestIdFilter.HEADER));
        UUID secondCorrelationId = UUID.fromString(second.getResponse().getHeader(RequestIdFilter.HEADER));
        assertNotEquals(clientCorrelationId, firstCorrelationId);
        assertNotEquals(clientCorrelationId, secondCorrelationId);
        assertNotEquals(firstCorrelationId, secondCorrelationId);
    }

    @Test
    void codesDistinguishMalformedParametersAndUnreadableBodiesAtTheSameStatus() throws Exception {
        MvcResult parameter = mvc.perform(get("/api/contract/parameter")
                .param("limit", "secret-invalid-input")).andExpect(status().isBadRequest()).andReturn();
        MvcResult body = mvc.perform(post("/api/contract/body").contentType(MediaType.APPLICATION_JSON)
                .content("{secret-invalid-body")).andExpect(status().isBadRequest()).andReturn();
        assertError(parameter, "INVALID_REQUEST", ErrorCode.INVALID_REQUEST.message());
        assertError(body, "REQUEST_BODY_UNREADABLE", ErrorCode.REQUEST_BODY_UNREADABLE.message());
    }

    @Test
    void ordinaryMissingParametersAndApplicationValidationUseInvalidRequest() throws Exception {
        MvcResult missing = mvc.perform(get("/api/contract/parameter"))
                .andExpect(status().isBadRequest()).andReturn();
        MvcResult application = mvc.perform(get("/api/contract/invalid"))
                .andExpect(status().isBadRequest()).andReturn();
        assertError(missing, "INVALID_REQUEST", ErrorCode.INVALID_REQUEST.message());
        assertError(application, "INVALID_REQUEST", ErrorCode.INVALID_REQUEST.message());
    }

    @Test
    void unknownFrameworkFailuresKeepStatusAndRetryHeaderWithoutExposingDetails() throws Exception {
        MvcResult result = mvc.perform(get("/api/contract/framework").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isTooManyRequests()).andExpect(header().string("Retry-After", "60"))
                .andReturn();
        assertError(result, "FRAMEWORK_REQUEST_REJECTED", ErrorCode.FRAMEWORK_REQUEST_REJECTED.message());
    }

    @Test
    void frameworkFallbackCodeIsIndependentOfStatus() throws Exception {
        MvcResult result = mvc.perform(get("/api/contract/framework-missing"))
                .andExpect(status().isNotFound()).andReturn();
        assertError(result, "FRAMEWORK_REQUEST_REJECTED", ErrorCode.FRAMEWORK_REQUEST_REJECTED.message());
    }

    @Test
    void unsupportedContentTypeKeepsSupportedTypeHeader() throws Exception {
        MvcResult result = mvc.perform(post("/api/contract/body").contentType(MediaType.TEXT_PLAIN)
                .content("secret-private-text")).andExpect(status().isUnsupportedMediaType())
                .andExpect(header().string("Accept", MediaType.APPLICATION_JSON_VALUE)).andReturn();
        assertError(result, "UNSUPPORTED_MEDIA_TYPE", ErrorCode.UNSUPPORTED_MEDIA_TYPE.message());
    }

    @ParameterizedTest(name = "committed {0} response stays untouched")
    @ValueSource(strings = {"framework", "unexpected", "application"})
    void committedResponsesAreNotRewritten(String kind) throws Exception {
        mvc.perform(get("/api/contract/committed-" + kind).accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isAccepted()).andExpect(header().doesNotExist("Content-Type"))
                .andExpect(content().string("already-written"));
    }

    @Test
    void typedAbsenceKeepsJsonDespiteRestrictiveAccept() throws Exception {
        MvcResult result = mvc.perform(get("/api/contract/missing").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound()).andReturn();
        assertError(result, "RESOURCE_NOT_FOUND", ErrorCode.RESOURCE_NOT_FOUND.message());
    }

    @Test
    void methodFailureKeepsAllowAndJsonDespiteRestrictiveAccept() throws Exception {
        MvcResult result = mvc.perform(post("/api/contract/failure").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isMethodNotAllowed()).andExpect(header().string("Allow", "GET"))
                .andReturn();
        assertError(result, "METHOD_NOT_ALLOWED", ErrorCode.METHOD_NOT_ALLOWED.message());
    }

    private void assertError(MvcResult result, String code, String message) throws Exception {
        assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());
        JsonNode error = json.readTree(result.getResponse().getContentAsString());
        assertEquals(3, error.size());
        assertEquals(code, error.path("code").asString());
        assertEquals(message, error.path("message").asString());
        assertEquals(UUID.fromString(result.getResponse().getHeader(RequestIdFilter.HEADER)),
                UUID.fromString(error.path("requestId").asString()));
    }

    // Advice/filter fixture only; outside production component scanning.
    @RestController
    static class ContractConsumer {
        @GetMapping("/api/contract/parameter")
        void parameter(@RequestParam int limit) {
        }

        @GetMapping("/api/contract/missing")
        void missing() {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        @GetMapping("/api/contract/invalid")
        void invalid() {
            throw new ApplicationException(ErrorCode.INVALID_REQUEST);
        }

        @PostMapping(value = "/api/contract/body", consumes = MediaType.APPLICATION_JSON_VALUE)
        void body(@RequestBody java.util.Map<String, String> body) {
        }

        @GetMapping("/api/contract/framework")
        void framework() {
            ErrorResponseException failure = new ErrorResponseException(HttpStatus.TOO_MANY_REQUESTS);
            failure.setDetail("private-resource-secret");
            failure.getHeaders().set("Retry-After", "60");
            throw failure;
        }

        @GetMapping("/api/contract/framework-missing")
        void frameworkMissing() {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "private-resource-secret");
        }

        @GetMapping("/api/contract/committed-{kind}")
        void committed(@PathVariable String kind, HttpServletResponse response) throws Exception {
            response.setStatus(202);
            response.getWriter().write("already-written");
            response.flushBuffer();
            switch (kind) {
                case "framework" -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "secret");
                case "application" -> throw new ApplicationException(ErrorCode.INVALID_REQUEST);
                default -> throw new IllegalStateException("private-document-text");
            }
        }

        @GetMapping("/api/contract/failure")
        void failure() {
            throw new IllegalStateException("SELECT password FROM accounts; secret private-document-text");
        }
    }

}
