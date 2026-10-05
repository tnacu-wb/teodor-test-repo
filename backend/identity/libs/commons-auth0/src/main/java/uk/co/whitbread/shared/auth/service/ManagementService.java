package uk.co.whitbread.shared.auth.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import com.auth0.net.TokenRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.mgmt.CustomManagementAPI;
import uk.co.whitbread.shared.auth.mgmt.json.JobErrors;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PROTECTED;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.RETRIEVE_TOKEN_ERROR_CODE;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.RETRIEVE_USERS_EMAIL_ERROR_CODE;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.USER_ALREADY_EXISTS_ERROR_CODE;

@Slf4j
public class ManagementService {

    @Getter
    private ManagementProperties managementProperties;

    @Getter(PROTECTED)
    @Setter(PROTECTED)
    private TokenHolder currentToken;

    @Getter(PROTECTED)
    @Setter(PROTECTED)
    private long currentTokenExpirationTime;

    @Getter(PROTECTED)
    @Setter(PROTECTED)
    private ManagementAPI currentManagementAPI;

    @Getter(PROTECTED)
    @Setter(PROTECTED)
    private CustomManagementAPI customManagementAPI;

    @Getter(PROTECTED)
    private AuthAPI authAPI;

    //2 minutes
    static final long MANAGEMENT_API_TOKEN_EXPIRY_MARGIN = 120000L;

    public ManagementService(ManagementProperties managementProperties) {
        this.managementProperties = managementProperties;
        authAPI = new AuthAPI(
                managementProperties.getDomain(),
                managementProperties.getClientId(),
                managementProperties.getClientSecret()
        );
    }

    protected synchronized void fetchAccessTokenForManagement() {

        if (!shouldFetchAccessTokenForManagement()) {
            return;
        }

        try {
            TokenRequest authRequest = getAuthAPI().requestToken(managementProperties.getAudience());
            TokenHolder tokenHolder = authRequest.execute().getBody();
            setCurrentToken(tokenHolder);
            setCurrentTokenExpirationTime(addToCurrentTime(tokenHolder.getExpiresIn()));
            setCurrentManagementAPI(new ManagementAPI(managementProperties.getDomain(), getCurrentToken().getAccessToken()));
            setCustomManagementAPI(new CustomManagementAPI(managementProperties.getDomain(), getCurrentToken().getAccessToken()));
        } catch (Auth0Exception e) {
            log.error("Auth0Exception", e);
            throw new AuthServiceException("Error while trying to retrieve token from Auth0", e)
                    .withErrorCode(RETRIEVE_TOKEN_ERROR_CODE.getCode());
        }
    }

    protected boolean shouldFetchAccessTokenForManagement() {

        if (getCurrentToken() == null || getCurrentManagementAPI() == null) {
            return true;
        }

        if ((getCurrentTime() + MANAGEMENT_API_TOKEN_EXPIRY_MARGIN) >= currentTokenExpirationTime) {
            return true;
        }

        return false;
    }

    protected long addToCurrentTime(long toAdd) {
        return getCurrentTime() + toAdd;
    }

    protected long getCurrentTime() {
        return currentTimeMillis();
    }

    public ManagementAPI retrieveManagementAPI() {

        if (shouldFetchAccessTokenForManagement()) {
            fetchAccessTokenForManagement();
        }

        return getCurrentManagementAPI();
    }

    public CustomManagementAPI retrieveCustomManagementAPI() {

        if (shouldFetchAccessTokenForManagement()) {
            fetchAccessTokenForManagement();
        }

        return getCustomManagementAPI();
    }

    public User getUserOrFail(String email) {

        Optional<User> user = getUser(email);
        if (!user.isPresent()) {
            log.error("Error while retrieving user by email from Auth0, user not found");
            throw new AuthServiceException("Error while retrieving user by email from Auth0, user not found")
                    .withErrorCode(RETRIEVE_USERS_EMAIL_ERROR_CODE.getCode());
        }
        return user.get();
    }

    protected boolean userHasConnection(User user) {

        boolean result =
                Optional.of(user)
                        .map(User::getIdentities)
                        .map(List::stream)
                        .orElse(Stream.empty())
                        .anyMatch(identity -> managementProperties.getConnection().equals(identity.getConnection()));
        return result;
    }


    public Optional<User> getUser(String email) {

        ManagementAPI managementAPI = retrieveManagementAPI();

        final Request<List<User>> listByEmailRequest = managementAPI.users().listByEmail(email, null);

        try {
            List<User> users = listByEmailRequest.execute().getBody();

            users = users.stream().filter(this::userHasConnection).collect(toList());

            if (users.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(users.get(0));

        } catch (Auth0Exception e) {
            log.error("Error while retrieving users by email from Auth0");
            throw new AuthServiceException("Error while retrieving users by email from Auth0", e)
                    .withErrorCode(RETRIEVE_USERS_EMAIL_ERROR_CODE.getCode());
        }

    }

    public void checkUserNotAlreadyInAuth0(String email) {

        Optional<User> user = getUser(email);
        if (user.isPresent()) {
            throw new AuthServiceException("User already exists")
                    .withErrorCode(USER_ALREADY_EXISTS_ERROR_CODE.getCode())
                    .withHttpStatus(HttpStatus.BAD_REQUEST);
        }
    }

    public JobErrors[] getJobErrorDetails(String jobId) {

        CustomManagementAPI customManagementAPI = retrieveCustomManagementAPI();

        final Request<JobErrors[]> jobErrorsRequest = customManagementAPI.jobErrors().get(jobId);

        try {
            return jobErrorsRequest.execute().getBody();
        } catch (Auth0Exception e) {
            log.error("Error while retrieving Auth0 jobs execution errors details");
            throw new AuthServiceException("Error while retrieving Auth0 jobs execution errors details", e);
        }
    }
}
