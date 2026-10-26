package contracts;

import java.security.Principal;
import java.util.UUID;

import com.aiknowledgebase.shared.AuthenticatedActor;
import com.aiknowledgebase.shared.web.ApiExceptionHandler;
import com.aiknowledgebase.shared.web.RequestIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        assertError(result, "BAD_REQUEST", "Bad Request");
    }

    @Test
    void explicitNotFoundDoesNotDisclosePrivateResourceOrExceptionReason() throws Exception {
        MvcResult result = mvc.perform(get("/api/contract/missing"))
                .andExpect(status().isNotFound()).andReturn();
        assertError(result, "NOT_FOUND", "Not Found");
    }

    @Test
    void unexpectedFailureDoesNotDiscloseSqlCredentialsOrPrivateContent() throws Exception {
        MvcResult first = mvc.perform(get("/api/contract/failure"))
                .andExpect(status().isInternalServerError()).andReturn();
        MvcResult second = mvc.perform(get("/api/contract/failure"))
                .andExpect(status().isInternalServerError()).andReturn();
        assertError(first, "INTERNAL_ERROR", "Internal Server Error");
        assertNotEquals(first.getResponse().getHeader(RequestIdFilter.HEADER),
                second.getResponse().getHeader(RequestIdFilter.HEADER));
    }

    @Test
    void mvcErrorsKeepStatusAndProtocolHeadersWhileUsingTheEnvelope() throws Exception {
        MvcResult result = mvc.perform(post("/api/contract/failure"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET"))
                .andReturn();
        assertError(result, "HTTP_405", "Method Not Allowed");
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "private-resource-secret");
        }

        @GetMapping("/api/contract/failure")
        void failure() {
            throw new IllegalStateException("SELECT password FROM accounts; secret private-document-text");
        }
    }

    record Ids(UUID accountId, UUID resourceId, UUID requestId) {
    }
}
