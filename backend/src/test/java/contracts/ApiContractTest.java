package contracts;

import java.security.Principal;
import java.util.UUID;

import com.aiknowledgebase.shared.ApplicationException;
import com.aiknowledgebase.shared.AuthenticatedActor;
import com.aiknowledgebase.shared.ErrorCode;
import com.aiknowledgebase.shared.web.ApiExceptionHandler;
import com.aiknowledgebase.shared.web.RequestIdFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void consumerUsesAuthenticatedAccountAndUuidResourceAndOperationIds() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        UUID operationRequestId = UUID.randomUUID();
        AuthenticatedActor actor = new AuthenticatedActor(accountId);

        MvcResult result = mvc.perform(get("/api/contract/{resourceId}", resourceId)
                        .principal(actor).param("requestId", operationRequestId.toString())
                        .header(RequestIdFilter.HEADER, "untrusted-client-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.resourceId").value(resourceId.toString()))
                .andExpect(jsonPath("$.requestId").value(operationRequestId.toString()))
                .andReturn();

        UUID correlationId = UUID.fromString(result.getResponse().getHeader(RequestIdFilter.HEADER));
        assertNotEquals(operationRequestId, correlationId);
        assertEquals(accountId.toString(), actor.getName());
        assertThrows(NullPointerException.class, () -> new AuthenticatedActor(null));
    }

    @Test
    void malformedUuidIsAUsefulSafeBadRequest() throws Exception {
        MvcResult result = mvc.perform(get("/api/contract/secret-invalid-input")
                        .param("requestId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest()).andReturn();
        assertError(result, "INVALID_REQUEST", ErrorCode.INVALID_REQUEST.message());
    }

    @Test
    void typedNotFoundUsesSafeResourceMessage() throws Exception {
        MvcResult result = mvc.perform(get("/api/contract/missing"))
                .andExpect(status().isNotFound()).andReturn();
        assertError(result, "RESOURCE_NOT_FOUND", ErrorCode.RESOURCE_NOT_FOUND.message());
    }

    @Test
    void unexpectedFailureDoesNotDiscloseSqlCredentialsOrPrivateContent() throws Exception {
        MvcResult first = mvc.perform(get("/api/contract/failure"))
                .andExpect(status().isInternalServerError()).andReturn();
        MvcResult second = mvc.perform(get("/api/contract/failure"))
                .andExpect(status().isInternalServerError()).andReturn();
        assertError(first, "INTERNAL_ERROR", ErrorCode.INTERNAL_ERROR.message());
        assertNotEquals(first.getResponse().getHeader(RequestIdFilter.HEADER),
                second.getResponse().getHeader(RequestIdFilter.HEADER));
    }

    @Test
    void mvcErrorsKeepStatusAndProtocolHeadersWhileUsingTheEnvelope() throws Exception {
        MvcResult result = mvc.perform(post("/api/contract/failure"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET"))
                .andReturn();
        assertError(result, "METHOD_NOT_ALLOWED", ErrorCode.METHOD_NOT_ALLOWED.message());
    }

    @Test
    void missingAndCrossOwnerResourcesHaveTheSamePublicFailure() throws Exception {
        MvcResult missing = mvc.perform(get("/api/contract/missing")).andReturn();
        MvcResult crossOwner = mvc.perform(get("/api/contract/cross-owner")).andReturn();
        assertEquals(missing.getResponse().getStatus(), crossOwner.getResponse().getStatus());
        assertError(missing, "RESOURCE_NOT_FOUND", ErrorCode.RESOURCE_NOT_FOUND.message());
        assertError(crossOwner, "RESOURCE_NOT_FOUND", ErrorCode.RESOURCE_NOT_FOUND.message());
    }

    @Test
    void codesDistinguishMalformedParametersAndUnreadableBodiesAtTheSameStatus() throws Exception {
        MvcResult parameter = mvc.perform(get("/api/contract/secret-invalid-input")
                .param("requestId", UUID.randomUUID().toString())).andExpect(status().isBadRequest()).andReturn();
        MvcResult body = mvc.perform(post("/api/contract/body").contentType(MediaType.APPLICATION_JSON)
                .content("{secret-invalid-body")).andExpect(status().isBadRequest()).andReturn();
        assertError(parameter, "INVALID_REQUEST", ErrorCode.INVALID_REQUEST.message());
        assertError(body, "REQUEST_BODY_UNREADABLE", ErrorCode.REQUEST_BODY_UNREADABLE.message());
    }

    @Test
    void ordinaryMissingParametersAndApplicationValidationUseInvalidRequest() throws Exception {
        MvcResult missing = mvc.perform(get("/api/contract/{id}", UUID.randomUUID()))
                .andExpect(status().isBadRequest()).andReturn();
        MvcResult application = mvc.perform(get("/api/contract/invalid"))
                .andExpect(status().isBadRequest()).andReturn();
        assertError(missing, "INVALID_REQUEST", ErrorCode.INVALID_REQUEST.message());
        assertError(application, "INVALID_REQUEST", ErrorCode.INVALID_REQUEST.message());
    }

    @Test
    void unknownFrameworkFailuresKeepStatusAndRetryHeaderWithoutExposingDetails() throws Exception {
        MvcResult result = mvc.perform(get("/api/contract/framework"))
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

    @Test
    void committedFrameworkAndUnexpectedResponsesAreNotRewritten() throws Exception {
        for (String path : new String[]{"committed-framework", "committed-unexpected", "committed-application"}) {
            mvc.perform(get("/api/contract/" + path)).andExpect(status().isAccepted())
                    .andExpect(content().string("already-written"));
        }
    }

    private void assertError(MvcResult result, String code, String message) throws Exception {
        JsonNode error = json.readTree(result.getResponse().getContentAsString());
        assertEquals(3, error.size());
        assertEquals(code, error.path("code").asString());
        assertEquals(message, error.path("message").asString());
        assertEquals(UUID.fromString(result.getResponse().getHeader(RequestIdFilter.HEADER)),
                UUID.fromString(error.path("requestId").asString()));
    }

    // Consumer fixture only: no identity, resource or operation endpoint is exposed by the app.
    @RestController
    static class ContractConsumer {
        @GetMapping("/api/contract/{resourceId}")
        Ids identifiers(Principal principal, @PathVariable UUID resourceId, @RequestParam UUID requestId) {
            return new Ids(((AuthenticatedActor) principal).accountId(), resourceId, requestId);
        }

        @GetMapping("/api/contract/missing")
        void missing() {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        @GetMapping("/api/contract/cross-owner")
        void crossOwner() {
            // Feature owners must use the same failure for absence and failed owner lookup.
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        @GetMapping("/api/contract/invalid")
        void invalid() {
            throw new ApplicationException(ErrorCode.INVALID_REQUEST);
        }

        @PostMapping(value = "/api/contract/body", consumes = MediaType.APPLICATION_JSON_VALUE)
        Ids body(@RequestBody Ids ids) {
            return ids;
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

    record Ids(UUID accountId, UUID resourceId, UUID requestId) {
    }
}
