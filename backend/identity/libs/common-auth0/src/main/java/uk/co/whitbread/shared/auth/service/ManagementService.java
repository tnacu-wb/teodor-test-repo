package uk.co.whitbread.shared.auth.service;

import static java.lang.System.currentTimeMillis;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.RETRIEVE_TOKEN_ERROR_CODE;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.RETRIEVE_USERS_EMAIL_ERROR_CODE;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.USER_ALREADY_EXISTS_ERROR_CODE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.exception.InvalidLoginException;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.model.PasswordResetRequest;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;
import uk.co.whitbread.shared.auth.webclient.WebClientRetryHandler;

/**
 * Facade over Auth0 Management API v2 using WebClient.
 */
@Slf4j
public class ManagementService {

    private static final String HTTPS_SCHEME = "https://";
    private static final String OAUTH_TOKEN_PATH = "/oauth/token";
    private static final String USERS_BY_EMAIL_PATH = "/api/v2/users-by-email";
    private static final String USERS_PATH = "/api/v2/users";
    private static final String USER_PATH = "/api/v2/users/{userId}";
    private static final String PASSWORD_CHANGE_PATH = "/api/v2/tickets/password-change";
    private static final String CUSTOM_DOMAIN_HEADER = "auth0-custom-domain";

    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
    private static final String PASSWORD_REALM_GRANT_TYPE = "http://auth0.com/oauth/grant-type/password-realm";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String AUDIENCE = "audience";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String REALM = "realm";
    private static final String CONNECTION = "connection";
    private static final String EMAIL_QUERY_PARAM = "email";
    private static final String SEARCH_QUERY_PARAM = "q";
    private static final String SEARCH_ENGINE_QUERY_PARAM = "search_engine";
    private static final String SEARCH_ENGINE_V3 = "v3";
    private static final String TICKET_FIELD = "ticket";

    private static final String TOKEN_RETRIEVAL_ERROR = "Error while trying to retrieve token from Auth0";
    private static final String USER_RETRIEVAL_ERROR = "Error while retrieving users by email from Auth0";
    private static final String USER_NOT_FOUND_ERROR = "Error while retrieving user by email from Auth0, user not found";
    private static final String USER_ALREADY_EXISTS_ERROR = "User already exists";
    private static final String USER_CREATION_ERROR = "Error while creating user in Auth0";
    private static final String USER_UPDATE_ERROR = "Error while updating user in Auth0";
    private static final String USER_DELETE_ERROR = "Error while deleting user in Auth0";
    private static final String USER_SEARCH_ERROR = "Error while searching users in Auth0";
    private static final String PASSWORD_CHANGE_ERROR = "Error while requesting password change ticket from Auth0";
    private static final String INVALID_CREDENTIALS_ERROR = "Invalid credentials";
    private static final String NULL_LOGIN_ARGUMENTS_ERROR = "email, password, and connection must not be null";

    @Getter
    private final ManagementProperties managementProperties;
    private final WebClient webClient;
    private final WebClientRetryHandler webClientRetryHandler;

    private String cachedAccessToken;
    private long tokenExpiresAt;

    static final long MANAGEMENT_API_TOKEN_EXPIRY_MARGIN = 120_000L;

    public ManagementService(ManagementProperties managementProperties,
                             WebClient.Builder webClientBuilder,
                             WebClientRetryHandler webClientRetryHandler) {
        this.managementProperties = managementProperties;
        this.webClient = webClientBuilder
                .baseUrl(HTTPS_SCHEME + managementProperties.getDomain())
                .build();
        this.webClientRetryHandler = webClientRetryHandler;
    }

    ManagementService(ManagementProperties managementProperties, WebClient webClient,
                      WebClientRetryHandler webClientRetryHandler) {
        this.managementProperties = managementProperties;
        this.webClient = webClient;
        this.webClientRetryHandler = webClientRetryHandler;
    }

    private synchronized String getValidToken() {
        if (cachedAccessToken == null
                || (currentTimeMillis() + MANAGEMENT_API_TOKEN_EXPIRY_MARGIN) >= tokenExpiresAt) {
            fetchAccessToken();
        }
        return cachedAccessToken;
    }

    private void fetchAccessToken() {
        try {
            TokenResponse token = webClient.post()
                    .uri(OAUTH_TOKEN_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(clientCredentialsRequestBody())
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();

            cachedAccessToken = token.accessToken();
            // expires_in is in seconds; convert to millis and add to current time
            tokenExpiresAt = currentTimeMillis() + (token.expiresIn() * 1000L);
        } catch (Exception e) {
            log.error(TOKEN_RETRIEVAL_ERROR, e);
            throw new AuthServiceException(TOKEN_RETRIEVAL_ERROR, e)
                    .withErrorCode(RETRIEVE_TOKEN_ERROR_CODE.getCode());
        }
    }

    public Optional<Auth0User> getUser(String email) {
        List<Auth0User> users = listUsersByEmail(email);

        List<Auth0User> filtered = users.stream()
                .filter(this::userHasConnection)
                .toList();

        if (filtered.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(filtered.get(0));
    }

    public Auth0User getUserOrFail(String email) {
        return getUser(email)
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND_ERROR);
                    return new AuthServiceException(USER_NOT_FOUND_ERROR)
                            .withErrorCode(RETRIEVE_USERS_EMAIL_ERROR_CODE.getCode());
                });
    }

    public void checkUserNotAlreadyInAuth0(String email) {
        Optional<Auth0User> user = getUser(email);
        if (user.isPresent()) {
            throw new AuthServiceException(USER_ALREADY_EXISTS_ERROR)
                    .withErrorCode(USER_ALREADY_EXISTS_ERROR_CODE.getCode())
                    .withHttpStatus(HttpStatus.BAD_REQUEST);
        }
    }

    protected boolean userHasConnection(Auth0User user) {
        return Optional.ofNullable(user.getIdentities())
                .map(List::stream)
                .orElse(Stream.empty())
                .anyMatch(identity ->
                        managementProperties.getConnection().equals(identity.get(CONNECTION)));
    }

    private List<Auth0User> listUsersByEmail(String email) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(USERS_BY_EMAIL_PATH)
                            .queryParam(EMAIL_QUERY_PARAM, email)
                            .build())
                    .headers(h -> h.setBearerAuth(getValidToken()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Auth0User>>() {})
                    .retryWhen(webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec())
                    .block();
        } catch (WebClientResponseException e) {
            log.error(USER_RETRIEVAL_ERROR);
            throw new AuthServiceException(USER_RETRIEVAL_ERROR, e)
                    .withErrorCode(RETRIEVE_USERS_EMAIL_ERROR_CODE.getCode());
        }
    }

    public Auth0User createUser(Auth0User user) {
        try {
            return webClient.post()
                    .uri(USERS_PATH)
                    .headers(h -> h.setBearerAuth(getValidToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(Auth0User.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw auth0ApiException(USER_CREATION_ERROR, e);
        }
    }

    public Auth0User updateUser(String userId, Auth0User user) {
        try {
            return webClient.patch()
                    .uri(USER_PATH, userId)
                    .headers(h -> h.setBearerAuth(getValidToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(Auth0User.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw auth0ApiException(USER_UPDATE_ERROR, e);
        }
    }

    public void deleteUser(String userId) {
        try {
            webClient.delete()
                    .uri(USER_PATH, userId)
                    .headers(h -> h.setBearerAuth(getValidToken()))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw auth0ApiException(USER_DELETE_ERROR, e);
        }
    }

    public List<Auth0User> listUsers(String query) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(USERS_PATH)
                            .queryParam(SEARCH_QUERY_PARAM, query)
                            .queryParam(SEARCH_ENGINE_QUERY_PARAM, SEARCH_ENGINE_V3)
                            .build())
                    .headers(h -> h.setBearerAuth(getValidToken()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Auth0User>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw auth0ApiException(USER_SEARCH_ERROR, e);
        }
    }

    public String requestPasswordChange(PasswordResetRequest request) {
        try {
            Map<String, Object> response = webClient.post()
                    .uri(PASSWORD_CHANGE_PATH)
                    .headers(h -> h.setBearerAuth(getValidToken()))
                    .header(CUSTOM_DOMAIN_HEADER, managementProperties.getCustomDomain())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            return (String) response.get(TICKET_FIELD);
        } catch (WebClientResponseException e) {
            throw auth0ApiException(PASSWORD_CHANGE_ERROR, e);
        }
    }

    public void login(String email, String password, String connection) {
        if (email == null || password == null || connection == null) {
            throw new IllegalArgumentException(NULL_LOGIN_ARGUMENTS_ERROR);
        }
        try {
            webClient.post()
                    .uri(OAUTH_TOKEN_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(passwordGrantRequestBody(email, password, connection))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.warn("Login verification failed for user {}: {}", email, e.getMessage());
            throw new InvalidLoginException(INVALID_CREDENTIALS_ERROR, e);
        }
    }

    private Map<String, String> clientCredentialsRequestBody() {
        return Map.of(
                GRANT_TYPE, CLIENT_CREDENTIALS_GRANT_TYPE,
                CLIENT_ID, managementProperties.getClientId(),
                CLIENT_SECRET, managementProperties.getClientSecret(),
                AUDIENCE, managementProperties.getAudience()
        );
    }

    private Map<String, String> passwordGrantRequestBody(String email, String password, String connection) {
        return Map.of(
                GRANT_TYPE, PASSWORD_REALM_GRANT_TYPE,
                USERNAME, email,
                PASSWORD, password,
                REALM, connection,
                CLIENT_ID, managementProperties.getClientId(),
                CLIENT_SECRET, managementProperties.getClientSecret()
        );
    }

    private Auth0ApiException auth0ApiException(String message, WebClientResponseException e) {
        log.error("{}: {}", message, e.getResponseBodyAsString(), e);
        return new Auth0ApiException(message, e.getStatusCode().value(), null, e.getResponseBodyAsString(), e);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") long expiresIn) {
    }
}
