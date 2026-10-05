package uk.co.whitbread.shared.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.whitbread.shared.auth.constants.ProfileParam;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.jwt.ProviderTokenVerifier;
import uk.co.whitbread.shared.auth.jwt.TokenExtractor;
import uk.co.whitbread.shared.auth.model.CCUIDetails;
import uk.co.whitbread.shared.auth.model.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.model.EmployeeDetails;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {

    private static final String JWT_TOKEN = "authorization";
    private static final String INVALID_TOKEN = "not a token";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CUSTOMER_ACCOUNT_ID = "ab106aac-d6bd-46ac-b847-7848bba9e1fa";
    private static final String COMPANY_ACCOUNT_ID = "9753cf43-d24d-4eba-863a-04aa2c2df099";
    private static final String EMPLOYEE_ACCOUNT_ID = "8e747067-59bf-46fd-92ab-a839e3a1bfbf";
    private static final String EMAIL = "dummy@email.com";
    private static final String CCUI = "CCUI";

    @Mock
    private ProviderTokenVerifier providerTokenVerifier1;
    @Mock
    private ProviderTokenVerifier providerTokenVerifier2;

    @Mock
    private TokenExtractor tokenExtractor;

    private TokenService tokenService;

    @Mock
    private DecodedJWT mockDecodeJwt;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ArgumentCaptor<String> propertyKeyCaptor1 = ArgumentCaptor.forClass(String.class);
    private ArgumentCaptor<String> propertyKeyCaptor2 = ArgumentCaptor.forClass(String.class);


    @Before
    public void setup(){
        tokenService = new TokenService(asList(providerTokenVerifier1, providerTokenVerifier2), tokenExtractor);
    }

    @Test
    public void retrieveAndVerifyToken_whenValid() {
        when(tokenExtractor.extractToken("Bearer authorization")).thenReturn(Optional.of("authorization"));
        when(providerTokenVerifier1.verifyAndDecodeToken("authorization")).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.SESSION_ID)).thenReturn(Optional.of("sessionid"));
        when(providerTokenVerifier2.verifyAndDecodeToken("authorization")).thenReturn(Optional.empty());

        Optional<String> result = tokenService.retrieveAndVerifyToken("Bearer authorization");

        assertThat("Not Null", result, notNullValue());
        assertThat("Result found", result.isPresent(), is(true));
        result.ifPresent(s -> assertThat("Session Id", s, is("sessionid")));

        verify(tokenExtractor).extractToken("Bearer authorization");
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.SESSION_ID);
        verify(providerTokenVerifier1, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        verify(providerTokenVerifier2, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
        assertThat("At least 1 token verifier called",  "authorization".equals(propertyKeyCaptor1.getValue()) || "authorization".equals(propertyKeyCaptor2.getValue()));


    }

    @Test
    public void retrieveAndVerifyToken_whenNotValid() {
        when(tokenExtractor.extractToken("Bearer invalid_authorization")).thenReturn(Optional.of("authorization"));
        when(providerTokenVerifier1.verifyAndDecodeToken("authorization")).thenReturn(Optional.empty());
        when(providerTokenVerifier2.verifyAndDecodeToken("authorization")).thenReturn(Optional.empty());

        expectedException.expect(TokenVerificationException.class);
        expectedException.expectMessage("Provided token was invalid or expired");

        try {
            tokenService.retrieveAndVerifyToken("Bearer invalid_authorization");
        }
        finally {
            verify(tokenExtractor).extractToken("Bearer invalid_authorization");
            verify(providerTokenVerifier1, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
            verify(providerTokenVerifier2, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
            assertThat("At least 1 token verifier called", "authorization".equals(propertyKeyCaptor1.getValue()) || "authorization".equals(propertyKeyCaptor2.getValue()));
        }
    }

    @Test
    public void retrieveAndVerifyToken_whenNoTokenPassed() {
        when(tokenExtractor.extractToken(INVALID_TOKEN)).thenReturn(Optional.empty());
        Optional<String> result = tokenService.retrieveAndVerifyToken(INVALID_TOKEN);

        assertThat("Not Null", result, notNullValue());

        assertThat("Result not found", result.isPresent(), is(false));


        verify(tokenExtractor).extractToken(INVALID_TOKEN);

    }

    @Test
    public void retrieveAndVerifyToken_whenExceptionThrown() {
        when(tokenExtractor.extractToken("Bearer authorization")).thenReturn(Optional.of("authorization"));

        doThrow(RuntimeException.class).when(providerTokenVerifier1).verifyAndDecodeToken("authorization");
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.SESSION_ID)).thenReturn(Optional.of("sessionid"));
        when(providerTokenVerifier2.verifyAndDecodeToken("authorization")).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.SESSION_ID)).thenReturn(Optional.of("sessionid"));

        Optional<String> result = tokenService.retrieveAndVerifyToken("Bearer authorization");

        assertThat("Not Null", result, notNullValue());

        assertThat("Result found", result.isPresent(), is(true));
        result.ifPresent(s -> assertThat("Session Id", s, is("sessionid")));

        verify(tokenExtractor).extractToken("Bearer authorization");
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.SESSION_ID);
        verify(providerTokenVerifier1, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        verify(providerTokenVerifier2, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
        assertThat("At least 1 token verifier called",  "authorization".equals(propertyKeyCaptor1.getValue()) || "authorization".equals(propertyKeyCaptor2.getValue()));
    }

    @Test
    public void retrieveEmailAndVerifyToken_whenValid() {
        when(tokenExtractor.extractToken("Bearer authorization")).thenReturn(Optional.of("authorization"));
        when(providerTokenVerifier1.verifyAndDecodeToken("authorization")).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.EMAIL)).thenReturn(Optional.of("test@mail.com"));
        when(providerTokenVerifier2.verifyAndDecodeToken("authorization")).thenReturn(Optional.empty());

        Optional<String> result = tokenService.retrieveEmailAndVerifyToken("Bearer authorization");

        assertThat("Not Null", result, notNullValue());

        assertThat("Result found", result.isPresent(), is(true));
        result.ifPresent(s -> assertThat("Email", s, is("test@mail.com")));

        verify(tokenExtractor).extractToken("Bearer authorization");
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.EMAIL);
        verify(providerTokenVerifier1, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        verify(providerTokenVerifier2, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
        assertThat("At least 1 token verifier called",  "authorization".equals(propertyKeyCaptor1.getValue()) || "authorization".equals(propertyKeyCaptor2.getValue()));
    }
    
    @Test
    public void retrieveEmployeeDetailsAndVerifyToken_whenValid() {
        when(tokenExtractor.extractToken("Bearer authorization")).thenReturn(Optional.of("authorization"));
        when(providerTokenVerifier1.verifyAndDecodeToken("authorization")).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.COMPANY_ID)).thenReturn(Optional.of("50"));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.EMPLOYEE_ID)).thenReturn(Optional.of("2"));
        when(providerTokenVerifier2.verifyAndDecodeToken("authorization")).thenReturn(Optional.empty());
    
        EmployeeDetails employeeDetails = tokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer authorization");
    
        assertThat("Not Null", employeeDetails, notNullValue());
    
        assertThat("companyId", employeeDetails.getCompanyId(), is("50"));
        assertThat("employeeId", employeeDetails.getEmployeeId(), is("2"));
    
        verify(tokenExtractor).extractToken("Bearer authorization");
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.COMPANY_ID);
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.EMPLOYEE_ID);
        verify(providerTokenVerifier1, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        verify(providerTokenVerifier2, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
        assertThat("At least 1 token verifier called",
                "authorization".equals(propertyKeyCaptor1.getValue()) || "authorization"
                        .equals(propertyKeyCaptor2.getValue()));
    }

    @Test
    public void retrieveCustomerAccountIdAndVerifyToken_whenValid() {
        when(tokenExtractor.extractToken(BEARER_PREFIX + JWT_TOKEN)).thenReturn(Optional.of(JWT_TOKEN));
        when(providerTokenVerifier1.verifyAndDecodeToken(JWT_TOKEN)).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.CUSTOMER_ACCOUNT_ID)).thenReturn(Optional.of(CUSTOMER_ACCOUNT_ID));
        when(providerTokenVerifier2.verifyAndDecodeToken(JWT_TOKEN)).thenReturn(Optional.empty());

        Optional<String> result = tokenService.retrieveCustomerAccountIdAndVerifyToken(BEARER_PREFIX + JWT_TOKEN);

        assertTrue(result.isPresent());
        assertEquals(CUSTOMER_ACCOUNT_ID, result.get());

        verify(tokenExtractor).extractToken(BEARER_PREFIX + JWT_TOKEN);
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.CUSTOMER_ACCOUNT_ID);
        verify(providerTokenVerifier1, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        verify(providerTokenVerifier2, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
        assertTrue(JWT_TOKEN.equals(propertyKeyCaptor1.getValue()) || JWT_TOKEN.equals(propertyKeyCaptor2.getValue()));
    }

    @Test
    public void retrieveCdhEmployeeDetailsAndVerifyToken_whenValid() {
        when(tokenExtractor.extractToken(BEARER_PREFIX + JWT_TOKEN)).thenReturn(Optional.of(JWT_TOKEN));
        when(providerTokenVerifier1.verifyAndDecodeToken(JWT_TOKEN)).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.COMPANY_ACCOUNT_ID)).thenReturn(Optional.of(COMPANY_ACCOUNT_ID));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.EMPLOYEE_ACCOUNT_ID)).thenReturn(Optional.of(EMPLOYEE_ACCOUNT_ID));
        when(providerTokenVerifier2.verifyAndDecodeToken(JWT_TOKEN)).thenReturn(Optional.empty());

        CdhEmployeeDetails cdhEmployeeDetails = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(BEARER_PREFIX + JWT_TOKEN);

        assertNotNull(cdhEmployeeDetails);
        assertEquals(COMPANY_ACCOUNT_ID, cdhEmployeeDetails.getCompanyAccountId());
        assertEquals(EMPLOYEE_ACCOUNT_ID, cdhEmployeeDetails.getEmployeeAccountId());

        verify(tokenExtractor).extractToken(BEARER_PREFIX + JWT_TOKEN);
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.COMPANY_ACCOUNT_ID);
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.EMPLOYEE_ACCOUNT_ID);
        verify(providerTokenVerifier1, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        verify(providerTokenVerifier2, atLeast(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
        assertTrue(JWT_TOKEN.equals(propertyKeyCaptor1.getValue()) || JWT_TOKEN.equals(propertyKeyCaptor2.getValue()));
    }

    @Test
    public void retrieveAndVerifyCCUIToken_whenNotValid_returnEmpty() {
        when(tokenExtractor.extractToken(INVALID_TOKEN))
            .thenReturn(Optional.empty());

        CCUIDetails ccuiDetails = tokenService.retrieveAndVerifyCCUIToken(INVALID_TOKEN);
        verify(tokenExtractor).extractToken(INVALID_TOKEN);
        verify(tokenExtractor, times(0)).retrieve(mockDecodeJwt, ProfileParam.CCUI_EMAIL);
        verify(tokenExtractor, times(0)).retrieve(mockDecodeJwt, ProfileParam.BOOKING_FLOW);
        verify(providerTokenVerifier1, times(0)).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        verify(providerTokenVerifier2, times(0)).verifyAndDecodeToken(propertyKeyCaptor2.capture());
        assertNull(ccuiDetails.getBookingFlow());
        assertNull(ccuiDetails.getEmail());
    }

    @Test
    public void retrieveAndVerifyCCUIToken_success() {
        when(tokenExtractor.extractToken(BEARER_PREFIX + JWT_TOKEN)).thenReturn(Optional.of(JWT_TOKEN));
        when(providerTokenVerifier1.verifyAndDecodeToken(JWT_TOKEN)).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.CCUI_EMAIL)).thenReturn(Optional.of(EMAIL));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.BOOKING_FLOW)).thenReturn(Optional.of(CCUI));

        CCUIDetails ccuiDetails = tokenService.retrieveAndVerifyCCUIToken(BEARER_PREFIX + JWT_TOKEN);

        assertThat(ProfileParam.CCUI_EMAIL.getParam(), ccuiDetails.getEmail(), is(EMAIL));
        assertThat(ProfileParam.BOOKING_FLOW.getParam(), ccuiDetails.getBookingFlow(), is(CCUI));

        verify(tokenExtractor).extractToken(BEARER_PREFIX + JWT_TOKEN);
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.CCUI_EMAIL);
        verify(tokenExtractor).retrieve(mockDecodeJwt, ProfileParam.BOOKING_FLOW);
        verify(providerTokenVerifier1).verifyAndDecodeToken(propertyKeyCaptor1.capture());
        assertEquals(JWT_TOKEN, propertyKeyCaptor1.getValue());
        assertThat("At least 1 token verifier called",
                "authorization".equals(propertyKeyCaptor1.getValue()) || "authorization"
                        .equals(propertyKeyCaptor2.getValue()));
    }

    @Test
    public void retrieveIssueAtAndVerifyToken_whenValid() {
        when(tokenExtractor.extractToken(BEARER_PREFIX + JWT_TOKEN)).thenReturn(Optional.of(JWT_TOKEN));
        when(providerTokenVerifier1.verifyAndDecodeToken(JWT_TOKEN)).thenReturn(Optional.of(mockDecodeJwt));
        when(tokenExtractor.retrieve(mockDecodeJwt, ProfileParam.ISSUED_AT)).thenReturn(Optional.of("1772721539"));
        when(providerTokenVerifier2.verifyAndDecodeToken(JWT_TOKEN)).thenReturn(Optional.empty());

        Optional<String> result = tokenService.retrieveIssueAtAndVerifyToken(BEARER_PREFIX + JWT_TOKEN);

        assertTrue(result.isPresent());
        assertEquals("1772721539", result.get());

    }
}