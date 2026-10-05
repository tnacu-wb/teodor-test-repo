package uk.co.whitbread.hotel.account.utils.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.bart.business.api.UserLogin;
import uk.co.whitbread.bart.business.api.UserLoginResponse;
import uk.co.whitbread.bart.business.auth0.api.InitSession;
import uk.co.whitbread.bart.business.auth0.api.InitSessionRequest;
import uk.co.whitbread.bart.business.auth0.api.InitSessionResponse;
import uk.co.whitbread.hotel.account.exceptions.InvalidLoginException;
import uk.co.whitbread.hotel.account.model.*;
import uk.co.whitbread.shared.auth.service.EncryptionService;

@Component
@RequiredArgsConstructor
public class BartBBAuthTransformer {

    private final LoginRequestMapper mapper;
    private final EncryptionService encryptionService;

    public UserLogin transform(LoginRequest request) {
        return mapper.toUserLogin(request);
    }

    public LoginResponse transform(UserLoginResponse response) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setSessionId(response.getUserLoginResult().getSessionID());
        loginResponse.setLoginSuccessful(true);
        return loginResponse;
    }

    public InitSession transform(SessionRequest request) {
        try {
            String plainGuestHistoryNumber = encryptionService.readSecuredMessage(request.getGuestHistoryNumber());
            InitSessionRequest initSessionRequest = new InitSessionRequest();
            initSessionRequest.setGuestHistoryNumber(plainGuestHistoryNumber);
            InitSession result = new InitSession();
            result.setRequest(initSessionRequest);
            return result;
        } catch (Exception ex) {
            throw new InvalidLoginException("Invalid guest history number");
        }
    }

    public SessionResponse transform(InitSessionResponse response) {
        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setSessionId(response.getInitSessionResult().getSessionID());
        sessionResponse.setCompanyId(response.getInitSessionResult().getCompanyID());
        sessionResponse.setEmployeeId(
            response.getInitSessionResult().getEmployee().getEmployeeID());
        sessionResponse.setAccessLevel(
            response.getInitSessionResult().getEmployee().getAccessLevel());
        return sessionResponse;
    }
}