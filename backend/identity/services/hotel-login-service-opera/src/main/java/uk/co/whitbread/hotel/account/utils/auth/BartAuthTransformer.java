package uk.co.whitbread.hotel.account.utils.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import uk.co.whitbread.bart.auth0.api.InitSession;
import uk.co.whitbread.bart.auth0.api.InitSessionRequest;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse;
import uk.co.whitbread.bart.booking.api.ClearSessionRequest;
import uk.co.whitbread.bart.booking.api.ClearSessionRequest2;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequest;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequestResponse;
import uk.co.whitbread.hotel.account.exceptions.InvalidLoginException;
import uk.co.whitbread.hotel.account.model.*;
import uk.co.whitbread.shared.auth.service.EncryptionService;

@Component
@RefreshScope
@RequiredArgsConstructor
public class BartAuthTransformer {

    private final LoginRequestMapper mapper;
    private final EncryptionService encryptionService;

    public RegisteredGuestLoginRequest transform(LoginRequest request) {
        RegisteredGuestLoginRequest bartRequest = mapper.toRegisteredGuestLoginRequest(request);
        bartRequest.getRegisteredGuestLoginDetails().setLoginMethod(LoginMethod.EMAIL.name());
        return bartRequest;
    }

    public InitSession transform(SessionRequest request) {
        try {
            String plainGuestHistoryNumber = encryptionService.readSecuredMessage(request.getGuestHistoryNumber());
            InitSessionRequest initSessionRequest = new InitSessionRequest();
            initSessionRequest.setGuestHistoryNumber(plainGuestHistoryNumber);
            InitSession result = new InitSession();
            result.setInitSessionRequest(initSessionRequest);
            return result;
        } catch (Exception ex) {
            throw new InvalidLoginException("Invalid guest history number");
        }
    }

    public SessionResponse transform(InitSessionResponse response) {
        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setSessionId(response.getInitSessionResult().getSessionID());
        return sessionResponse;
    }

    public LoginResponse transform(RegisteredGuestLoginRequestResponse response) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setSessionId(response.getRegisteredGuestLoginRequestResult().getSessionID());
        loginResponse.setLoginSuccessful(true);
        return loginResponse;
    }

    public ClearSessionRequest transform(String sessionID) {
        ClearSessionRequest request = new ClearSessionRequest();
        ClearSessionRequest2 innerRequest = new ClearSessionRequest2();
        innerRequest.setSessionID(sessionID);
        request.setClearSessionDetails(innerRequest);
        return request;
    }
}
