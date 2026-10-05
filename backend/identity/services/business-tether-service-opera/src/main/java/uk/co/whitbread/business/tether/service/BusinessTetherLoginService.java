package uk.co.whitbread.business.tether.service;

import static uk.co.whitbread.business.tether.utils.SanitizingUtils.sanitize;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.business.tether.converter.WorldlineTransformer;
import uk.co.whitbread.business.tether.exception.BusinessTetherException;
import uk.co.whitbread.business.tether.model.LoginCriteria;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetheredLoginResponse;
import uk.co.whitbread.business.tether.utils.SessionTokenUtil;
import uk.co.whitbread.business.tether.utils.WorldlineUtils;
import uk.co.whitbread.business.tether.validation.WorldLineTetherResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponse;
import worldline.mst.bsm.api.b2b.pi.data.RefreshSession;
import worldline.mst.bsm.api.b2b.pi.data.RefreshSessionRequestType;
import worldline.mst.bsm.api.b2b.pi.data.RefreshSessionResponse;
import worldline.mst.bsm.api.b2b.pi.data.SessionTokenType;

@Service
@Slf4j
@AllArgsConstructor
public class BusinessTetherLoginService {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    private final WebServiceTemplate worldlineWebServiceTemplate;
    private final WorldLineProperties worldLineProperties;
    private final WorldlineTransformer worldlineBusinessTetherTransformer;
    private final WorldLineTetherResponseValidator worldLineTetherResponseValidator;
    private final WorldLineWebServiceMessageCallback worldLineWebServiceMessageCallback;
    private final WorldlineUtils worldlineUtils;

    public TetheredLoginResponse login(LoginCriteria criteria) {

        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        try {
            Scheme scheme = getScheme(criteria.getScheme());

            if (criteria.getWorldlineSessionId() != null && criteria.getWorldlineSharedSecret() != null) {
                log.info("Called BusinessTetherLoginService.login. Refresh session with worldlineSessionId={}",
                        sanitize(criteria.getWorldlineSessionId()));
                String worldLineSessionId = criteria.getWorldlineSessionId();
                String worldLineSharedSecret = criteria.getWorldlineSharedSecret();
                worldlineRefreshSession(worldLineSharedSecret, worldLineSessionId, timestamp, scheme);

                return createTetheredLoginResponse(timestamp, worldLineSessionId, worldLineSharedSecret);

            } else {
                log.info("Called BusinessTetherLoginService.login. guid={}", sanitize(criteria.getGuid()));
                LoginTetheredUserResponse loginTetheredUserResponse = worldLineLoginTetheredUser(criteria.getGuid(), scheme);

                String worldLineSessionId = loginTetheredUserResponse.getResponse().getNewSession().getSessionId();
                String worldLineSharedSecret = loginTetheredUserResponse.getResponse().getNewSession().getSharedSecret();

                return createTetheredLoginResponse(timestamp, worldLineSessionId, worldLineSharedSecret);
            }
        } catch (SoapFaultClientException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("Worldline tether login as failed with the following error: ", e);
            throw new BusinessTetherException(e.getMessage());
        }
    }

    private Scheme getScheme(String scheme) {
        return null!=scheme?Scheme.valueOf(scheme):Scheme.GB;
    }

    private TetheredLoginResponse createTetheredLoginResponse(String timestamp, String worldLineSessionId, String worldLineSharedSecret)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String nonce = SessionTokenUtil.generateNonceGuidBase64();
        TetheredLoginResponse tetheredLoginResponse = new TetheredLoginResponse();
        tetheredLoginResponse.setHash(SessionTokenUtil.generateHashedSessionToken(worldLineSharedSecret, timestamp, nonce));
        tetheredLoginResponse.setNonce(nonce);
        tetheredLoginResponse.setSessionId(worldLineSessionId);
        tetheredLoginResponse.setSharedSecret(worldLineSharedSecret);
        tetheredLoginResponse.setTimestamp(timestamp);
        return tetheredLoginResponse;
    }

    private LoginTetheredUserResponse worldLineLoginTetheredUser(String guid, Scheme scheme) throws SoapFaultClientException {

        LoginTetheredUserResponse response = (LoginTetheredUserResponse) dispatchWorldLineRequest(
                    worldlineBusinessTetherTransformer.toLoginTetheredUserRequest(guid, scheme));

        worldLineTetherResponseValidator.validate(response).ifPresent(error -> {
            throw new BusinessTetherException(error).withErrorCode(response.getResponse().getResultCode());
        });

        return response;
    }

    private void worldlineRefreshSession(String sharedSecret, String sessionId, String timestamp, Scheme scheme)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {

        RefreshSessionResponse response = (RefreshSessionResponse) dispatchWorldLineRequest(
                    buildRefreshSessionRequestType(sharedSecret, sessionId, timestamp, scheme));

        worldLineTetherResponseValidator.validate(response).ifPresent(error -> {
            throw new BusinessTetherException(error).withErrorCode(response.getResponse().getResultCode());
        });
    }

    private RefreshSession buildRefreshSessionRequestType(String sharedSecret, String sessionId, String timestamp, Scheme scheme)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        RefreshSession request = new RefreshSession();
        RefreshSessionRequestType innerRequest = new RefreshSessionRequestType();
        SessionTokenType sessionToken = SessionTokenUtil.generateSessionToken(sharedSecret, sessionId, timestamp);
        HeaderType header = new HeaderType();
        header.setClientMessageId(getClientMessageId(scheme));
        header.setCultureCode(getCultureCode(scheme));
        innerRequest.setHeader(header);
        innerRequest.setSessionToken(sessionToken);
        request.setRequest(innerRequest);

        return request;
    }

    private String getCultureCode(Scheme scheme) {
        return Scheme.DE.equals(scheme)?worldLineProperties.getDe().getCultureCode():worldLineProperties.getGb().getCultureCode();
    }

    private String getClientMessageId(Scheme scheme) {
        return Scheme.DE.equals(scheme)?worldLineProperties.getDe().getClientMessageId():worldLineProperties.getGb().getClientMessageId();
    }

    private <T> Object dispatchWorldLineRequest(T requestObject) {
        log.info("Sending WorldlineRequest for {} = {} #####",
            requestObject.getClass().getSimpleName(), worldlineUtils.serializeObject(requestObject));
        var response = worldlineWebServiceTemplate.marshalSendAndReceive(
            worldLineProperties.getPiba().getService().getUrl(),
            requestObject,
            worldLineWebServiceMessageCallback
        );
        log.info("Received WorldlineResponse for {} = {} #####",
            response.getClass().getSimpleName(), worldlineUtils.serializeObject(response));
        return response;
    }

}
