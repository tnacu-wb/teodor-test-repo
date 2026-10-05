package uk.co.whitbread.shared.auth.service;


import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.Identity;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.shared.auth.constants.ErrorCodes;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.mgmt.CustomManagementAPI;
import uk.co.whitbread.shared.auth.mgmt.JobErrorsEntity;
import uk.co.whitbread.shared.auth.mgmt.json.JobErrors;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManagementServiceTest {

    private static final String TOKEN_XYZ = "token_XYZ";
    private static final String VALID_EMAIL = "http://validUrl.co.uk";
    private static final String GUEST_HISTORY_NUMBER_LABEL = "guestHistoryNumber";
    private static final Object GHN_TEST = "GHN_test";
    public static final String DOMAIN = "domain-test";
    public static final String CLIENT_ID = "client-id-test";
    public static final String CLIENT_SECRET = "client-secret-test";
    public static final String AUDIENCE = "audience-test";
    public static final String CONNECTION = "connection-test";
    public static final String OTHER_CONNECTION = "other-connection-test";
    public static final long CURRENT_TIME = 1643120148152L;
    public static final long EXPIRES_IN = 1500000L;
    private static final long MANAGEMENT_API_TOKEN_EXPIRY_MARGIN = 120000L;
    private static final String JOB_ID = "job_b8RnjPMH9zoQcY3J";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private AuthAPI authAPI;

    @Mock
    private ManagementAPI managementAPI;

    @Mock
    private CustomManagementAPI customManagementAPI;

    @Mock
    private UsersEntity usersEntity;

    @Mock
    private JobErrorsEntity jobErrorsEntity;

    private ManagementProperties managementProperties = new ManagementProperties();

    @Mock
    private TokenHolder tokenHolder;

    @Mock
    private TokenRequest authRequest;

    @Mock
    private Response<TokenHolder> tokenResponse;

    @Mock
    private Request<List<User>> userRequest;

    @Mock
    private Response<List<User>> userResponse;

    @Mock
    private Request<JobErrors[]> jobErrorsRequest;

    @Mock
    private Response<JobErrors[]> jobErrorsResponse;

    @Spy
    private User auth0User;

    @Mock
    private Identity identity;

    @Spy
    private JobErrors jobErrors;

    private ManagementService target;


    @Before
    public void setUp() throws Auth0Exception {
        managementProperties.setDomain(DOMAIN);
        managementProperties.setClientId(CLIENT_ID);
        managementProperties.setClientSecret(CLIENT_SECRET);
        managementProperties.setAudience(AUDIENCE);
        managementProperties.setConnection(CONNECTION);

        target = spy(new ManagementService(managementProperties));

        target.setCurrentToken(null);
        target.setCurrentManagementAPI(null);
        target.setCustomManagementAPI(null);
        target.setCurrentTokenExpirationTime(0L);

        when(authAPI.requestToken(AUDIENCE)).thenReturn(authRequest);
        when(authRequest.execute()).thenReturn(tokenResponse);
        when(tokenResponse.getBody()).thenReturn(tokenHolder);
        doReturn(authAPI).when(target).getAuthAPI();
        when(tokenHolder.getAccessToken()).thenReturn(TOKEN_XYZ);
        when(tokenHolder.getExpiresIn()).thenReturn(EXPIRES_IN);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.listByEmail(VALID_EMAIL, null)).thenReturn(userRequest);

        when(customManagementAPI.jobErrors()).thenReturn(jobErrorsEntity);
        when(jobErrorsEntity.get(JOB_ID)).thenReturn(jobErrorsRequest);

        auth0User.setPassword("password1");
        auth0User.setEmail(VALID_EMAIL);
        doReturn(singletonList(identity)).when(auth0User).getIdentities();
        when(identity.getConnection()).thenReturn(CONNECTION);

        Map<String, Object> map = new HashMap<>();
        map.put(GUEST_HISTORY_NUMBER_LABEL, GHN_TEST);
        auth0User.setUserMetadata(map);

        doReturn(CURRENT_TIME).when(target).getCurrentTime();
    }

    @Test
    public void shouldNotFetchAccessTokenForManagementWhenAlreadyDone() {
        doReturn(false).when(target).shouldFetchAccessTokenForManagement();
        target.fetchAccessTokenForManagement();
        verify(target, never()).getAuthAPI();
        verify(authAPI, never()).requestToken(anyString());
    }

    @Test
    public void shouldFetchAccessTokenForManagement() {
        doReturn(true).when(target).shouldFetchAccessTokenForManagement();
        target.fetchAccessTokenForManagement();
        verify(target).setCurrentToken(tokenHolder);
        verify(target).setCurrentTokenExpirationTime(CURRENT_TIME + EXPIRES_IN);
        assertNotNull(target.getCurrentManagementAPI());
    }


    @Test
    public void fetchAccessTokenShouldReturnAuth0Exception() throws Auth0Exception {
        doReturn(true).when(target).shouldFetchAccessTokenForManagement();

        Auth0Exception ex = new Auth0Exception("Auth0 exception");
        when(authRequest.execute()).thenThrow(ex);

        exception.expect(AuthServiceException.class);
        exception.expectMessage("Error while trying to retrieve token from Auth0");
        exception.expectCause(sameInstance(ex));

        target.fetchAccessTokenForManagement();
    }

    @Test
    public void shouldRetrieveNotExpiredManagementAPI() {
        doReturn(managementAPI).when(target).getCurrentManagementAPI();
        doReturn(false).when(target).shouldFetchAccessTokenForManagement();

        ManagementAPI result = target.retrieveManagementAPI();
        assertEquals(managementAPI, result);
        verify(target, never()).fetchAccessTokenForManagement();
    }

    @Test
    public void shouldRetrieveExpiredManagementAPI() {
        doReturn(managementAPI).when(target).getCurrentManagementAPI();
        doReturn(true).when(target).shouldFetchAccessTokenForManagement();

        ManagementAPI result = target.retrieveManagementAPI();
        assertEquals(managementAPI, result);
        verify(target).fetchAccessTokenForManagement();
    }

    @Test
    public void shouldRetrieveNotExpiredCustomManagementAPI() {
        doReturn(customManagementAPI).when(target).getCustomManagementAPI();
        doReturn(false).when(target).shouldFetchAccessTokenForManagement();

        CustomManagementAPI result = target.retrieveCustomManagementAPI();
        assertEquals(customManagementAPI, result);
        verify(target, never()).fetchAccessTokenForManagement();
    }

    @Test
    public void shouldRetrieveExpiredCustomManagementAPI() {
        doReturn(customManagementAPI).when(target).getCustomManagementAPI();
        doReturn(true).when(target).shouldFetchAccessTokenForManagement();

        CustomManagementAPI result = target.retrieveCustomManagementAPI();
        assertEquals(customManagementAPI, result);
        verify(target).fetchAccessTokenForManagement();
    }

    @Test
    public void shouldFetchAccessTokenForManagementWhenCurrentTokenNull() {
        target.setCurrentToken(null);
        boolean result = target.shouldFetchAccessTokenForManagement();
        assertTrue(result);
    }

    @Test
    public void shouldFetchAccessTokenForManagementWhenCurrentManagementApiNull() {
        target.setCurrentManagementAPI(null);
        boolean result = target.shouldFetchAccessTokenForManagement();
        assertTrue(result);
    }

    @Test
    public void shouldFetchAccessTokenForManagementWhenCloseToExpiring() {
        target.setCurrentToken(tokenHolder);
        target.setCurrentManagementAPI(managementAPI);
        target.setCurrentTokenExpirationTime(CURRENT_TIME + EXPIRES_IN);
        doReturn(CURRENT_TIME + EXPIRES_IN - MANAGEMENT_API_TOKEN_EXPIRY_MARGIN).when(target).getCurrentTime();
        boolean result = target.shouldFetchAccessTokenForManagement();
        assertTrue(result);
    }

    @Test
    public void shouldFetchAccessTokenForManagementWhenExpired() {
        target.setCurrentToken(tokenHolder);
        target.setCurrentManagementAPI(managementAPI);
        target.setCurrentTokenExpirationTime(CURRENT_TIME - 1);
        boolean result = target.shouldFetchAccessTokenForManagement();
        assertTrue(result);
    }

    @Test
    public void shouldNotFetchAccessTokenForManagement() {
        target.setCurrentToken(tokenHolder);
        target.setCurrentManagementAPI(managementAPI);
        target.setCurrentTokenExpirationTime(CURRENT_TIME + EXPIRES_IN);
        boolean result = target.shouldFetchAccessTokenForManagement();
        assertFalse(result);
    }


    @Test
    public void getManagementAPIShouldReturnError() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("'domain' cannot be null!");
        managementProperties.setDomain(null);
        target.retrieveManagementAPI();
    }

    @Test
    public void getCustomManagementAPIShouldReturnError() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("'domain' cannot be null!");
        managementProperties.setDomain(null);
        target.retrieveCustomManagementAPI();
    }

    @Test
    public void shouldCheckUserNotAlreadyInAuth0() throws Auth0Exception {
        doReturn(managementAPI).when(target).retrieveManagementAPI();

        when(userRequest.execute()).thenReturn(userResponse);
        when(userResponse.getBody()).thenReturn(emptyList());
        target.checkUserNotAlreadyInAuth0(VALID_EMAIL);
    }

    @Test
    public void shouldCheckUserNotAlreadyInAuth0IfExistsInAnotherConnection() throws Auth0Exception {
        doReturn(managementAPI).when(target).retrieveManagementAPI();

        when(identity.getConnection()).thenReturn(OTHER_CONNECTION);
        when(userRequest.execute()).thenReturn(userResponse);
        when(userResponse.getBody()).thenReturn(singletonList(auth0User));

        target.checkUserNotAlreadyInAuth0(VALID_EMAIL);
    }

    @Test
    public void shouldCheckUserNotAlreadyInAuth0AndThrowExceptionWhenUserExists() throws Auth0Exception {
        doReturn(managementAPI).when(target).retrieveManagementAPI();

        when(userRequest.execute()).thenReturn(userResponse);
        when(userResponse.getBody()).thenReturn(singletonList(auth0User));

        try {
            target.checkUserNotAlreadyInAuth0(VALID_EMAIL);
            fail();
        } catch (AuthServiceException e) {
            assertEquals("User already exists", e.getMessage());
            assertEquals(ErrorCodes.USER_ALREADY_EXISTS_ERROR_CODE.getCode(), e.getErrorCode());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        }
    }

    @Test
    public void shouldCheckUserNotAlreadyInAuth0AndThrowExceptionWhenAuth0Error() throws Auth0Exception {
        doReturn(managementAPI).when(target).retrieveManagementAPI();

        Auth0Exception auth0Exception = new Auth0Exception("Error while retrieving email from Auth0");

        when(userRequest.execute())
                .thenThrow(auth0Exception);
        exception.expect(AuthServiceException.class);
        exception.expectMessage("Error while retrieving users by email from Auth0");
        exception.expectCause(sameInstance(auth0Exception));

        target.checkUserNotAlreadyInAuth0(VALID_EMAIL);
    }

    @Test
    public void shouldCheckJobErrorsNotNull() throws Auth0Exception {
        doReturn(customManagementAPI).when(target).retrieveCustomManagementAPI();

        JobErrors errorDetails[] = new JobErrors[1];
        Arrays.fill(errorDetails, jobErrors);

        when(jobErrorsRequest.execute()).thenReturn(jobErrorsResponse);
        when(jobErrorsResponse.getBody()).thenReturn(errorDetails);
        assertNotNull(target.getJobErrorDetails(JOB_ID));
    }
}