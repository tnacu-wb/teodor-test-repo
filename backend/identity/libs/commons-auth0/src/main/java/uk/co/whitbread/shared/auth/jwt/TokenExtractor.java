package uk.co.whitbread.shared.auth.jwt;

import java.util.Optional;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import uk.co.whitbread.shared.auth.constants.ProfileParam;

public class TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer";
    private static final String PROFILE_CLAIM = "profile";

    /**
     * Checks the presence of a JWT token inside the authorization header given in parameter.
     * If found, returns the JWT token as a String.
     *
     * @param authorization The authorization header of the request.
     * @return A String Optional containing the extracted token, Optional.empty() otherwise.
     */
    public Optional<String> extractToken(String authorization) {
        String[] authorisationParts = Optional.ofNullable(authorization)
                .map(String::trim)
                .map(authorizationString -> authorizationString.split("\\s+"))
                .orElse(new String[0]);

        if (authorisationParts.length >= 2 && BEARER_PREFIX.equals(authorisationParts[0])) {
            return Optional.ofNullable(authorisationParts[1]);
        }
        return Optional.empty();
    }



    /**
     * Checks the presence of a name claim inside the decoded JWT given in parameter.
     * If found, returns the email as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted email, Optional.empty() otherwise.
     */
    public Optional<String> retrieveEmail(DecodedJWT decodedToken ) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.EMAIL.getParam()))
                .map(Claim::asString);
    }

    /**
     * Checks the presence of a name claim inside the decoded JWT given in parameter.
     * If found, returns the issuedAt as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted issuedAt, Optional.empty() otherwise.
     */
    public Optional<String> retrieveIssuedAt(DecodedJWT decodedToken ) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.ISSUED_AT.getParam()))
            .map(Claim::asDate)
            .map(date -> date.toInstant().toString());
    }
    
    
    /**
     * Checks the presence of a name claim inside the decoded JWT given in parameter.
     * If found, returns the companyId as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted companyId, Optional.empty() otherwise.
     */
    public Optional<String> retrieveCompanyId(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(PROFILE_CLAIM))
                .map(Claim::asMap)
                .map(profile -> profile.get(ProfileParam.COMPANY_ID.getParam()))
                .filter(companyId -> companyId instanceof String)
                .map(companyId -> (String) companyId);
    }
    
    
    /**
     * Checks the presence of a name claim inside the decoded JWT given in parameter.
     * If found, returns the employeeId as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted employeeId, Optional.empty() otherwise.
     */
    public Optional<String> retrieveEmployeeId(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(PROFILE_CLAIM))
                .map(Claim::asMap)
                .map(profile -> profile.get(ProfileParam.EMPLOYEE_ID.getParam()))
                .filter(employeeId -> employeeId instanceof String)
                .map(employeeId -> (String) employeeId);
    }
    
    /**
     * Checks the presence of a session ID claim inside the decoded JWT given in parameter.
     * If found, returns the session ID as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted session ID, Optional.empty() otherwise.
     */
    public Optional<String> retrieveSessionId(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(PROFILE_CLAIM))
                .map(Claim::asMap)
                .map(profile -> profile.get(ProfileParam.SESSION_ID.getParam()))
                .filter(sessionId -> sessionId instanceof String)
                .map(sessionId -> (String) sessionId);
    }

    /**
     * Checks the presence of a customer account ID claim inside the decoded JWT given in parameter.
     * If found, returns the customer account ID as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted customer account ID, Optional.empty() otherwise.
     */
    public Optional<String> retrieveCustomerAccountId(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.CUSTOMER_ACCOUNT_ID.getParam()))
            .map(Claim::asString);
    }

    /**
     * Checks the presence of a company account ID claim inside the decoded JWT given in parameter.
     * If found, returns the company account ID as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted company account ID, Optional.empty() otherwise.
     */
    public Optional<String> retrieveCompanyAccountId(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.COMPANY_ACCOUNT_ID.getParam()))
            .map(Claim::asString);
    }

    /**
     * Checks the presence of an employee account ID claim inside the decoded JWT given in parameter.
     * If found, returns the employee account ID as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted employee account ID, Optional.empty() otherwise.
     */
    public Optional<String> retrieveEmployeeAccountId(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.EMPLOYEE_ACCOUNT_ID.getParam()))
            .map(Claim::asString);
    }

    /**
     * Checks the presence of a user email claim inside the decoded JWT given in parameter.
     * If found, returns the email as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted email, Optional.empty() otherwise.
     */
    public Optional<String> retrieveUserEmail(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.USER_EMAIL.getParam()))
            .map(Claim::asString);
    }

    /**
     * Checks the presence of a CCUI email claim inside the decoded JWT given in parameter.
     * If found, returns the email as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted email, Optional.empty() otherwise.
     */
    public Optional<String> retrieveCCUIEmail(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.CCUI_EMAIL.getParam()))
                .map(Claim::asString);
    }

    /**
     * Checks the presence of a bookingFlow claim inside the decoded JWT given in parameter.
     * If found, returns the bookingFlow as a String.
     *
     * @param decodedToken A decoded token usually retrieved after validation.
     * @return A String Optional containing the extracted bookingFlow, Optional.empty() otherwise.
     */
    public Optional<String> retrieveBookingFlow(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(ProfileParam.BOOKING_FLOW.getParam()))
                .map(Claim::asString);
    }

    public Optional<String> retrieveAccessLevel(DecodedJWT decodedToken) {
        return Optional.ofNullable(decodedToken.getClaim(PROFILE_CLAIM))
            .map(Claim::asMap)
            .map(profile -> profile.get(ProfileParam.ACCESS_LEVEL.getParam()))
            .filter(accessLevel -> accessLevel instanceof String)
            .map(accessLevel -> (String) accessLevel);
    }

    public Optional<String> retrieve(DecodedJWT decodedJWT, ProfileParam profileParam) {
        if (ProfileParam.EMAIL.equals(profileParam)) {
            return retrieveEmail(decodedJWT);
        } else if (ProfileParam.COMPANY_ID.equals(profileParam)) {
            return retrieveCompanyId(decodedJWT);
        } else if (ProfileParam.EMPLOYEE_ID.equals(profileParam)) {
            return retrieveEmployeeId(decodedJWT);
        } else if (ProfileParam.CUSTOMER_ACCOUNT_ID.equals(profileParam)) {
            return retrieveCustomerAccountId(decodedJWT);
        } else if (ProfileParam.COMPANY_ACCOUNT_ID.equals(profileParam)) {
            return retrieveCompanyAccountId(decodedJWT);
        } else if (ProfileParam.EMPLOYEE_ACCOUNT_ID.equals(profileParam)) {
            return retrieveEmployeeAccountId(decodedJWT);
        } else if (ProfileParam.USER_EMAIL.equals(profileParam)) {
            return retrieveUserEmail(decodedJWT);
        } else if (ProfileParam.CCUI_EMAIL.equals(profileParam)) {
            return retrieveCCUIEmail(decodedJWT);
        } else if (ProfileParam.BOOKING_FLOW.equals(profileParam)) {
            return retrieveBookingFlow(decodedJWT);
        } else if (ProfileParam.ACCESS_LEVEL.equals(profileParam)) {
            return retrieveAccessLevel(decodedJWT);
        } else if (ProfileParam.ISSUED_AT.equals(profileParam)) {
            return retrieveIssuedAt(decodedJWT);
        }
        return retrieveSessionId(decodedJWT);
    }
}
