package uk.co.whitbread.shared.auth.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;
import org.mockito.Mockito;
import uk.co.whitbread.shared.auth.constants.ProfileParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TokenExtractorTest {


    private static final String SESSION_ID_CLAIM = "sessionId";
    private static final String SESSIONID = "sessionidtest";
    private static final String EMAIL_CLAIM = "name";
    private static final String COMPANY_CLAIM = "companyId";
    private static final String EMPLOYEE_CLAIM = "employeeId";
    private static final String CUSTOMER_ACCOUNT_ID_CLAIM = "https://premierinn.com/customerAccountId";
    private static final String COMPANY_ACCOUNT_ID_CLAIM = "https://premierinn.com/companyAccountId";
    private static final String EMPLOYEE_ACCOUNT_ID_CLAIM = "https://premierinn.com/employeeAccountId";
    private static final String EMAIL = "test@mail.com";
    private static final String COMPANY_ID = "50";
    private static final String EMPLOYEE_ID = "2";
    private static final String CUSTOMER_ACCOUNT_ID = "ab106aac-d6bd-46ac-b847-7848bba9e1fa";
    private static final String COMPANY_ACCOUNT_ID = "9753cf43-d24d-4eba-863a-04aa2c2df099";
    private static final String EMPLOYEE_ACCOUNT_ID = "8e747067-59bf-46fd-92ab-a839e3a1bfbf";

    private static final String PROFILE_CLAIM = "profile";
    private static final String AUTHORIZATION = "Bearer randomText";
    private static final String TOKEN = "randomText";
    private static final String INVALID_AUTHORIZATION = "Invalid randomText";
    private static final int INVALID_SESSIONID_CLAIM = 1;


    private TokenExtractor tokenExtractor = new TokenExtractor();

    @Test
    public void retrieveSessionId_returnValidSessionId() {
        DecodedJWT decodedJWT = createDecodedJWT(SESSIONID);

        Optional<String> sessionId = tokenExtractor.retrieve(decodedJWT, ProfileParam.SESSION_ID);

        assertEquals(SESSIONID, sessionId.get());
    }

    @Test
    public void retrieveEmail_returnValidEmail() {
        DecodedJWT decodedJWT = createEmailDecodedJWT(EMAIL);

        Optional<String> email = tokenExtractor.retrieve(decodedJWT, ProfileParam.EMAIL);

        assertEquals(EMAIL, email.get());
    }
    
    @Test
    public void retrieveCompanyId_returnValidCompanyId() {
        DecodedJWT decodedJWT = createDecodedJWT(COMPANY_ID);
        
        Optional<String> companyId = tokenExtractor.retrieve(decodedJWT, ProfileParam.COMPANY_ID);
        
        assertEquals(COMPANY_ID, companyId.get());
    }
    
    @Test
    public void retrieveEmployeeId_returnValidEmail() {
        DecodedJWT decodedJWT = createDecodedJWT(EMPLOYEE_ID);
        
        Optional<String> employeeId = tokenExtractor.retrieve(decodedJWT, ProfileParam.EMPLOYEE_ID);
        
        assertEquals(EMPLOYEE_ID, employeeId.get());
    }

    @Test
    public void retrieveSessionId_returnEmptyForInvalidClaim() {
        DecodedJWT decodedJWT = createDecodedJWT(INVALID_SESSIONID_CLAIM);

        Optional<String> sessionId = tokenExtractor.retrieve(decodedJWT, ProfileParam.SESSION_ID);

        assertEquals(Optional.empty(), sessionId);
    }

    @Test
    public void extractToken_returnValidString() {
        Optional<String> token = tokenExtractor.extractToken(AUTHORIZATION);

        assertEquals(TOKEN, token.get());
    }

    @Test
    public void extractToken_returnEmptyForNoAuthorization() {
        Optional<String> token = tokenExtractor.extractToken(null);

        assertEquals(Optional.empty(), token);
    }

    @Test
    public void extractToken_returnEmptyForInvalidPrefix() {
        Optional<String> token = tokenExtractor.extractToken(INVALID_AUTHORIZATION);

        assertEquals(Optional.empty(), token);
    }

    @Test
    public void retrieveCustomerAccountId_returnValidCustomerAccountId() {
        DecodedJWT decodedJWT = createCustomerAccountIdDecodedJWT(CUSTOMER_ACCOUNT_ID);

        Optional<String> customerAccountId = tokenExtractor.retrieve(decodedJWT, ProfileParam.CUSTOMER_ACCOUNT_ID);

        assertTrue(customerAccountId.isPresent());
        assertEquals(CUSTOMER_ACCOUNT_ID, customerAccountId.get());
    }

    @Test
    public void retrieveCompanyAccountId_returnValidCompanyAccountId() {
        DecodedJWT decodedJWT = createCompanyAccountIdDecodedJWT(COMPANY_ACCOUNT_ID);

        Optional<String> companyAccountId = tokenExtractor.retrieve(decodedJWT, ProfileParam.COMPANY_ACCOUNT_ID);

        assertTrue(companyAccountId.isPresent());
        assertEquals(COMPANY_ACCOUNT_ID, companyAccountId.get());
    }

    @Test
    public void retrieveEmployeeAccountId_returnValidCompanyAccountId() {
        DecodedJWT decodedJWT = createEmployeeAccountIdDecodedJWT(EMPLOYEE_ACCOUNT_ID);

        Optional<String> employeeAccountId = tokenExtractor.retrieve(decodedJWT, ProfileParam.EMPLOYEE_ACCOUNT_ID);

        assertTrue(employeeAccountId.isPresent());
        assertEquals(EMPLOYEE_ACCOUNT_ID, employeeAccountId.get());
    }

    private DecodedJWT createDecodedJWT(Object claimElement) {
        DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
        Claim claim = Mockito.mock(Claim.class);
        Map claimMap = new HashMap();
        claimMap.put(SESSION_ID_CLAIM, claimElement);
        claimMap.put(COMPANY_CLAIM, claimElement);
        claimMap.put(EMPLOYEE_CLAIM, claimElement);
        Mockito.when(claim.asMap()).thenReturn(claimMap);
        Mockito.when(decodedJWT.getClaim(PROFILE_CLAIM)).thenReturn(claim);

        return decodedJWT;
    }

    private DecodedJWT createEmailDecodedJWT(String claimElement) {
        DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
        Claim claim = Mockito.mock(Claim.class);
        Mockito.when(decodedJWT.getClaim(EMAIL_CLAIM)).thenReturn(claim);
        Mockito.when(claim.asString()).thenReturn(claimElement);
        return decodedJWT;
    }

    private DecodedJWT createCustomerAccountIdDecodedJWT(String claimElement) {
        DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
        Claim claim = Mockito.mock(Claim.class);
        Mockito.when(decodedJWT.getClaim(CUSTOMER_ACCOUNT_ID_CLAIM)).thenReturn(claim);
        Mockito.when(claim.asString()).thenReturn(claimElement);
        return decodedJWT;
    }

    private DecodedJWT createCompanyAccountIdDecodedJWT(String claimElement) {
        DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
        Claim claim = Mockito.mock(Claim.class);
        Mockito.when(decodedJWT.getClaim(COMPANY_ACCOUNT_ID_CLAIM)).thenReturn(claim);
        Mockito.when(claim.asString()).thenReturn(claimElement);
        return decodedJWT;
    }

    private DecodedJWT createEmployeeAccountIdDecodedJWT(String claimElement) {
        DecodedJWT decodedJWT = Mockito.mock(DecodedJWT.class);
        Claim claim = Mockito.mock(Claim.class);
        Mockito.when(decodedJWT.getClaim(EMPLOYEE_ACCOUNT_ID_CLAIM)).thenReturn(claim);
        Mockito.when(claim.asString()).thenReturn(claimElement);
        return decodedJWT;
    }
}