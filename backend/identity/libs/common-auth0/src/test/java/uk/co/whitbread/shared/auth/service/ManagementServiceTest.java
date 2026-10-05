package uk.co.whitbread.shared.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.RetriesExhaustedException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.model.PasswordResetRequest;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;
import uk.co.whitbread.shared.auth.webclient.WebClientRetryHandler;

class ManagementServiceTest {

  private MockWebServer mockServer;
  private ManagementService managementService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    ManagementProperties props = new ManagementProperties();
    props.setDomain("localhost");
    props.setClientId("test-client-id");
    props.setClientSecret("test-client-secret");
    props.setAudience("https://test.auth0.com/api/v2/");
    props.setConnection("Username-Password-Authentication");

    WebClient webClient = WebClient.builder()
        .baseUrl(mockServer.url("/").toString())
        .build();
    managementService = new ManagementService(props, webClient, new WebClientRetryHandler());
  }

  @AfterEach
  void tearDown() throws IOException {
    mockServer.shutdown();
  }

  private void enqueueTokenResponse() {
    mockServer.enqueue(new MockResponse()
        .setBody("{\"access_token\":\"mgmt-token\",\"expires_in\":86400}")
        .addHeader("Content-Type", "application/json"));
  }

  // --- Token caching ---

  @Test
  void getUser_fetchesTokenThenCallsApi() throws Exception {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse()
        .setBody("[]")
        .addHeader("Content-Type", "application/json"));

    managementService.getUser("user@example.com");

    assertEquals(2, mockServer.getRequestCount());
    RecordedRequest tokenReq = mockServer.takeRequest();
    assertEquals("/oauth/token", tokenReq.getPath());
    RecordedRequest usersReq = mockServer.takeRequest();
    assertTrue(usersReq.getPath().contains("/api/v2/users-by-email"));
    assertEquals("Bearer mgmt-token", usersReq.getHeader("Authorization"));
  }

  @Test
  void getUser_reusesToken_onSecondCall() throws Exception {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse()
        .setBody("[]")
        .addHeader("Content-Type", "application/json"));
    mockServer.enqueue(new MockResponse()
        .setBody("[]")
        .addHeader("Content-Type", "application/json"));

    managementService.getUser("a@b.com");
    managementService.getUser("c@d.com");

    // 1 token request + 2 user requests = 3
    assertEquals(3, mockServer.getRequestCount());
  }

  // --- getUser ---

  @Test
  void getUser_userFound_withMatchingConnection_returnsUser() throws Exception {
    enqueueTokenResponse();
    Auth0User user = Auth0User.builder()
        .id("auth0|123")
        .email("user@example.com")
        .identities(List.of(Map.of("connection", "Username-Password-Authentication")))
        .build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(List.of(user)))
        .addHeader("Content-Type", "application/json"));

    var result = managementService.getUser("user@example.com");

    assertTrue(result.isPresent());
    assertEquals("auth0|123", result.get().getId());
  }

  @Test
  void getUser_userFound_wrongConnection_returnsEmpty() throws Exception {
    enqueueTokenResponse();
    Auth0User user = Auth0User.builder()
        .id("auth0|123")
        .identities(List.of(Map.of("connection", "google-oauth2")))
        .build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(List.of(user)))
        .addHeader("Content-Type", "application/json"));

    var result = managementService.getUser("user@example.com");

    assertTrue(result.isEmpty());
  }

  @Test
  void getUser_noUsers_returnsEmpty() throws Exception {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse()
        .setBody("[]")
        .addHeader("Content-Type", "application/json"));

    var result = managementService.getUser("missing@example.com");

    assertTrue(result.isEmpty());
  }

  // --- getUserOrFail ---

  @Test
  void getUserOrFail_userNotFound_throwsAuthServiceException() {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse()
        .setBody("[]")
        .addHeader("Content-Type", "application/json"));

    assertThrows(AuthServiceException.class,
        () -> managementService.getUserOrFail("missing@example.com"));
  }

  // --- checkUserNotAlreadyInAuth0 ---

  @Test
  void checkUserNotAlreadyInAuth0_userExists_throwsAuthServiceException() throws Exception {
    enqueueTokenResponse();
    Auth0User user = Auth0User.builder()
        .id("auth0|123")
        .identities(List.of(Map.of("connection", "Username-Password-Authentication")))
        .build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(List.of(user)))
        .addHeader("Content-Type", "application/json"));

    AuthServiceException ex = assertThrows(AuthServiceException.class,
        () -> managementService.checkUserNotAlreadyInAuth0("existing@example.com"));
    assertEquals("7003", ex.getErrorCode());
  }

  @Test
  void checkUserNotAlreadyInAuth0_userDoesNotExist_succeeds() {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse()
        .setBody("[]")
        .addHeader("Content-Type", "application/json"));

    managementService.checkUserNotAlreadyInAuth0("new@example.com");
    // no exception = success
  }

  // --- createUser ---

  @Test
  void createUser_success_returnsCreatedUser() throws Exception {
    enqueueTokenResponse();
    Auth0User created = Auth0User.builder().id("auth0|new").email("new@test.com").build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(created))
        .addHeader("Content-Type", "application/json"));

    Auth0User input = Auth0User.builder().email("new@test.com").password("secret").build();
    Auth0User result = managementService.createUser(input);

    assertNotNull(result);
    assertEquals("auth0|new", result.getId());

    // Verify the POST request
    mockServer.takeRequest(); // token
    RecordedRequest createReq = mockServer.takeRequest();
    assertEquals("POST", createReq.getMethod());
    assertEquals("/api/v2/users", createReq.getPath());
  }

  @Test
  void createUser_conflict_throwsAuth0ApiException() {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse().setResponseCode(409).setBody("Conflict"));

    Auth0User input = Auth0User.builder().email("dup@test.com").build();
    assertThrows(Auth0ApiException.class, () -> managementService.createUser(input));
  }

  // --- updateUser ---

  @Test
  void updateUser_success_returnsUpdatedUser() throws Exception {
    enqueueTokenResponse();
    Auth0User updated = Auth0User.builder().id("auth0|123").email("new@test.com").build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(updated))
        .addHeader("Content-Type", "application/json"));

    Auth0User patch = Auth0User.builder().email("new@test.com").build();
    Auth0User result = managementService.updateUser("auth0|123", patch);

    assertEquals("new@test.com", result.getEmail());

    mockServer.takeRequest(); // token
    RecordedRequest updateReq = mockServer.takeRequest();
    assertEquals("PATCH", updateReq.getMethod());
    assertTrue(updateReq.getPath().contains("/api/v2/users/"));
  }

  // --- deleteUser ---

  @Test
  void deleteUser_success_completesWithoutException() throws Exception {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse().setResponseCode(204));

    managementService.deleteUser("auth0|123");

    mockServer.takeRequest(); // token
    RecordedRequest deleteReq = mockServer.takeRequest();
    assertEquals("DELETE", deleteReq.getMethod());
    assertTrue(deleteReq.getPath().contains("/api/v2/users/"));
  }

  @Test
  void deleteUser_notFound_throwsAuth0ApiException() {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse().setResponseCode(404).setBody("Not Found"));

    assertThrows(Auth0ApiException.class, () -> managementService.deleteUser("auth0|missing"));
  }

  // --- listUsers ---

  @Test
  void listUsers_success_returnsList() throws Exception {
    enqueueTokenResponse();
    Auth0User user = Auth0User.builder().id("auth0|1").email("a@b.com").build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(List.of(user)))
        .addHeader("Content-Type", "application/json"));

    List<Auth0User> result = managementService.listUsers("app_metadata.token:\"abc\"");

    assertEquals(1, result.size());
    assertEquals("auth0|1", result.get(0).getId());

    mockServer.takeRequest(); // token
    RecordedRequest searchReq = mockServer.takeRequest();
    assertTrue(searchReq.getPath().contains("/api/v2/users"));
    assertTrue(searchReq.getPath().contains("search_engine=v3"));
  }

  // --- requestPasswordChange ---

  @Test
  void requestPasswordChange_success_returnsTicketUrl() throws Exception {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse()
        .setBody("{\"ticket\":\"https://auth0.com/reset?ticket=abc123\"}")
        .addHeader("Content-Type", "application/json"));

    PasswordResetRequest request = PasswordResetRequest.builder()
        .userId("auth0|123")
        .resultUrl("https://app.com/reset-done")
        .markEmailAsVerified(true)
        .ttlSeconds(432000)
        .build();
    String ticket = managementService.requestPasswordChange(request);

    assertEquals("https://auth0.com/reset?ticket=abc123", ticket);

    mockServer.takeRequest(); // token
    RecordedRequest pwReq = mockServer.takeRequest();
    assertEquals("POST", pwReq.getMethod());
    assertEquals("/api/v2/tickets/password-change", pwReq.getPath());
  }

  // --- login ---

  @Test
  void login_validCredentials_succeeds() {
    mockServer.enqueue(new MockResponse()
        .setBody("{\"access_token\":\"user-token\"}")
        .addHeader("Content-Type", "application/json"));

    managementService.login("user@test.com", "password123", "Username-Password-Authentication");
    // no exception = success
  }

  @Test
  void login_invalidCredentials_throwsInvalidLoginException() {
    mockServer.enqueue(new MockResponse().setResponseCode(403).setBody("Wrong credentials"));

    assertThrows(InvalidLoginException.class,
        () -> managementService.login("user@test.com", "wrong",
            "Username-Password-Authentication"));
  }

  @Test
  void login_nullInput_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> managementService.login(null, "password123", "Username-Password-Authentication"));
    assertThrows(IllegalArgumentException.class,
        () -> managementService.login("user@test.com", null, "Username-Password-Authentication"));
    assertThrows(IllegalArgumentException.class,
        () -> managementService.login("user@test.com", "password123", null));
  }

  // --- listUsersByEmail retry ---

  @Test
  void listUsersByEmail_retriesOnConnectionFailure_eventuallySucceeds() throws Exception {
    enqueueTokenResponse();
    // 2 connection failures followed by a successful response
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    Auth0User user = Auth0User.builder()
        .id("auth0|123")
        .email("user@example.com")
        .identities(List.of(Map.of("connection", "Username-Password-Authentication")))
        .build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(List.of(user)))
        .addHeader("Content-Type", "application/json"));

    var result = managementService.getUser("user@example.com");

    assertTrue(result.isPresent());
    assertEquals("auth0|123", result.get().getId());
  }

  @Test
  void listUsersByEmail_exhaustsRetries_throwsRetriesExhaustedException() {
    enqueueTokenResponse();
    // 4 disconnects: 1 initial attempt + 3 retries all fail
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));

    RetriesExhaustedException ex = assertThrows(RetriesExhaustedException.class,
        () -> managementService.getUser("user@example.com"));

    assertNotNull(ex);
  }

  @Test
  void listUsersByEmail_serverError_retriesAndExhausts_throwsRetriesExhaustedException() {
    enqueueTokenResponse();
    // 1 initial attempt + 3 retries all return 500
    mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
    mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
    mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
    mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

    RetriesExhaustedException ex = assertThrows(RetriesExhaustedException.class,
        () -> managementService.getUser("user@test.com"));

    assertNotNull(ex);
    assertEquals(5, mockServer.getRequestCount());
  }

  @Test
  void listUsersByEmail_retries3Times_onNetworkFailure() throws Exception {
    enqueueTokenResponse();
    // Enqueue 3 connection failures (initial attempt + 2 retries that fail)
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    // 4th attempt succeeds
    Auth0User user = Auth0User.builder()
        .id("auth0|456")
        .email("retry@test.com")
        .identities(List.of(Map.of("connection", "Username-Password-Authentication")))
        .build();
    mockServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(List.of(user)))
        .addHeader("Content-Type", "application/json"));

    var result = managementService.getUser("retry@test.com");

    assertTrue(result.isPresent());
    assertEquals("auth0|456", result.get().getId());
    // Verify we made 5 requests: 1 token + 4 user requests (1 initial + 3 retries)
    assertEquals(5, mockServer.getRequestCount());
  }

  @Test
  void listUsersByEmail_clientError400_doesNotRetry_throwsAuthServiceException() {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));

    AuthServiceException ex = assertThrows(AuthServiceException.class,
        () -> managementService.getUser("invalid@test.com"));

    assertNotNull(ex);
    // Only 2 requests expected: 1 token + 1 user request (no retries on 400)
    assertEquals(2, mockServer.getRequestCount());
  }

  @Test
  void listUsersByEmail_clientError401_doesNotRetry_throwsAuthServiceException() {
    enqueueTokenResponse();
    mockServer.enqueue(new MockResponse().setResponseCode(401).setBody("Unauthorized"));

    AuthServiceException ex = assertThrows(AuthServiceException.class,
        () -> managementService.getUser("unauthorized@test.com"));

    assertNotNull(ex);
    // Only 2 requests expected: 1 token + 1 user request (no retries on 401)
    assertEquals(2, mockServer.getRequestCount());
  }

  @Test
  void listUsersByEmail_networkFailureThenServerError_exhaustsRetries() {
    enqueueTokenResponse();
    // First attempt: network failure
    mockServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
    // Next 3 attempts: 500 server errors until retries are exhausted
    mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
    mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
    mockServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

    RetriesExhaustedException ex = assertThrows(RetriesExhaustedException.class,
        () -> managementService.getUser("test@test.com"));

    assertNotNull(ex);
    // 1 token + 4 user requests (1 initial + 3 retries)
    assertEquals(5, mockServer.getRequestCount());
  }

}
