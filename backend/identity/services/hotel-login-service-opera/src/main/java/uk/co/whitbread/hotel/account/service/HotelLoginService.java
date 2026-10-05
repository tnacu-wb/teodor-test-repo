package uk.co.whitbread.hotel.account.service;

import static uk.co.whitbread.hotel.account.utils.BartErrors.BART_INVOCATION_ERROR_CODE;
import static uk.co.whitbread.hotel.account.utils.BartErrors.BART_RETURNED_ERROR_CODE;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse;
import uk.co.whitbread.bart.booking.api.ClearSessionRequestResponse;
import uk.co.whitbread.bart.business.api.UserLoginResponse;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequestResponse;
import uk.co.whitbread.bart.security.BartHeadersMessageCallback;
import uk.co.whitbread.bart.security.LoginWebServiceMessageCallback;
import uk.co.whitbread.hotel.account.exceptions.InvalidLoginException;
import uk.co.whitbread.hotel.account.model.LoginRequest;
import uk.co.whitbread.hotel.account.model.LoginResponse;
import uk.co.whitbread.hotel.account.model.LogoutRequest;
import uk.co.whitbread.hotel.account.model.LogoutResponse;
import uk.co.whitbread.hotel.account.model.SessionRequest;
import uk.co.whitbread.hotel.account.model.SessionResponse;
import uk.co.whitbread.hotel.account.properties.BartProperties;
import uk.co.whitbread.hotel.account.service.auth0.Auth0Service;
import uk.co.whitbread.hotel.account.utils.auth.BartAuthResponseValidator;
import uk.co.whitbread.hotel.account.utils.auth.BartAuthTransformer;
import uk.co.whitbread.hotel.account.utils.auth.BartBBAuthTransformer;

@Slf4j
@Service
@AllArgsConstructor
public class HotelLoginService {

    private final BartAuthTransformer bartAuthTransformer;
    private final BartBBAuthTransformer bartBBAuthTransformer;
    private final WebServiceTemplate webServiceTemplate;
    private final BartProperties bartProperties;
    private final LoginWebServiceMessageCallback loginServiceCallback;
    private final BartAuthResponseValidator bartAuthResponseValidator;
    @Qualifier("auth0LeisureService")
    private final Auth0Service auth0LeisureService;
    @Qualifier("auth0BusinessService")
    private final Auth0Service auth0BusinessService;

    public LoginResponse login(LoginRequest request, boolean business) {
        try {
            LoginResponse response;
            if (!business) {
                response = bartAuthTransformer.transform(myPILogin(request));
            }
            else {
                response = bartBBAuthTransformer.transform(bbLogin(request));
            }

            return response;
        } catch (SoapFaultClientException ex) {
            throw new BartServiceException(ex).withErrorCode(BART_INVOCATION_ERROR_CODE);
        }
    }

    private UserLoginResponse bbLogin(LoginRequest request) {
        UserLoginResponse response =
                (UserLoginResponse) webServiceTemplate.marshalSendAndReceive(
                        bartProperties.getBbClientServiceUrl(),
                        bartBBAuthTransformer.transform(request),
                        loginServiceCallback);

        bartAuthResponseValidator.validate(response).ifPresent(error -> {
            throw new InvalidLoginException(error);
        });

        auth0BusinessService.saveUserInAuth0(
                request.getUsername(),
                request.getPassword(),
                response.getUserLoginResult().getEmployee().getGhNumber());
        return response;
    }

    private RegisteredGuestLoginRequestResponse myPILogin(LoginRequest request) {
        RegisteredGuestLoginRequestResponse response =
                (RegisteredGuestLoginRequestResponse) webServiceTemplate.marshalSendAndReceive(
                        bartProperties.getPiGuestServiceUrl(),
                        bartAuthTransformer.transform(request),
                        loginServiceCallback);

        bartAuthResponseValidator.validate(response).ifPresent(error -> {
            throw new InvalidLoginException(error);
        });

        auth0LeisureService.saveUserInAuth0(
            request.getUsername(),
            request.getPassword(),
            response.getRegisteredGuestLoginRequestResult().getGuestDetails().getGuestHistoryNumber());
        return response;
    }

    public SessionResponse initiateSession(SessionRequest request, boolean business) {
        SessionResponse response;
        if (!business) {
            response = bartAuthTransformer.transform(myPiInitiateSession(request));
        } else {
            response = bartBBAuthTransformer.transform(bbInitiateSession(request));
        }
        return response;
    }

    private InitSessionResponse myPiInitiateSession(SessionRequest request) {
        InitSessionResponse response =
                (InitSessionResponse) webServiceTemplate.marshalSendAndReceive(
                        bartProperties.getPiInitSessionAuth0ServiceUrl(),
                        bartAuthTransformer.transform(request),
                        createBartHeaders());

        bartAuthResponseValidator.validate(response).ifPresent(error -> {
            throw new InvalidLoginException(error);
        });

        return response;
    }

    private uk.co.whitbread.bart.business.auth0.api.InitSessionResponse bbInitiateSession(SessionRequest request) {
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse response =
                (uk.co.whitbread.bart.business.auth0.api.InitSessionResponse) webServiceTemplate.marshalSendAndReceive(
                        bartProperties.getBbInitSessionAuth0ServiceUrl(),
                        bartBBAuthTransformer.transform(request),
                        createBartHeaders());

        bartAuthResponseValidator.validate(response).ifPresent(error -> {
            throw new InvalidLoginException(error);
        });

        return response;
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        try {
            ClearSessionRequestResponse response = (ClearSessionRequestResponse) webServiceTemplate
                    .marshalSendAndReceive(bartProperties.getBartBookingServiceUrl(),
                            bartAuthTransformer.transform(logoutRequest.getSessionId()), loginServiceCallback);

            bartAuthResponseValidator.validate(response).ifPresent(error -> {
                throw new BartServiceException(error).withErrorCode(BART_RETURNED_ERROR_CODE);
            });

            return new LogoutResponse(true);
        } catch (SoapFaultClientException ex) {
            throw new BartServiceException(ex).withErrorCode(BART_INVOCATION_ERROR_CODE);
        }
    }

    private BartHeadersMessageCallback createBartHeaders() {
        return new BartHeadersMessageCallback(bartProperties.getInitSessionUsername(), bartProperties.getInitSessionPassword());
    }
}
