package uk.co.whitbread.shared.auth.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.auth.PasswordlessEmailType;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.PasswordlessEmailResponse;
import com.auth0.json.auth.TokenHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.properties.PasswordlessProperties;

import static uk.co.whitbread.shared.auth.constants.ErrorCodes.EXCHANGE_OTP_ERROR_CODE;
import static uk.co.whitbread.shared.auth.constants.ErrorCodes.TRIGGER_SENDING_OTP_ERROR_CODE;

/**
 * Used for authenticating users via a one-time password (OTP) sent to their emails.
 * After the OTP has been received, it can be exchanged for a JWT.
 * This JWT can be used by any microservice that implements {@link TokenService} to validate tokens.
 */
@Slf4j
@Data
public class PasswordlessService {

    private PasswordlessProperties properties;

    private AuthAPI auth;

    private final static String REALM_TYPE = "email";

    public PasswordlessService(PasswordlessProperties properties) {
        this.properties = properties;
        this.auth = new AuthAPI(properties.getDomain(), properties.getClientId(), properties.getClientSecret());
    }

    /**
     * Triggers sending a one-time passcode to a given email address.
     * @param email to send an OTP to
     * @return PasswordlessEmailResponse confirming the email address the OTP was sent to as well as its verification status.
     */
    public PasswordlessEmailResponse sendOTP(String email) {
        log.info("Attempting to send OTP.");
        try {
            var response = auth.startPasswordlessEmailFlow(email, PasswordlessEmailType.CODE)
                    .execute()
                    .getBody();
            log.debug("Email: {}", response.getEmail());
            log.debug("Id: {}", response.getId());
            log.debug("Email verified: {}", response.isEmailVerified());
            return response;
        } catch (Auth0Exception e) {
            log.error("Error triggering sending of OTP.");
            throw new AuthServiceException("Could not trigger sending of OTP.", e)
                    .withErrorCode(TRIGGER_SENDING_OTP_ERROR_CODE.getCode());
        }
    }

    /**
     * Exchanges the previously sent OTP for a login token (JWT).
     * @param email - Email that received the OTP
     * @param code - The OTP that was sent
     * @return TokenHolder with id_token (JWT).
     */
    public TokenHolder exchangeOTP(String email, String code) {
        var request = auth.exchangePasswordlessOtp(email, REALM_TYPE, code.toCharArray());
        try {
            return request.execute().getBody();
        } catch (Auth0Exception e) {
            log.error("Error exchanging OTP for token.");
            throw new AuthServiceException("Could not exchange OTP for token.", e)
                    .withErrorCode(EXCHANGE_OTP_ERROR_CODE.getCode());
        }
    }

}
