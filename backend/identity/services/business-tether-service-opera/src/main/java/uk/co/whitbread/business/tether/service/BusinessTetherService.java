package uk.co.whitbread.business.tether.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.business.tether.converter.WorldlineTransformer;
import uk.co.whitbread.business.tether.exception.BusinessTetherException;
import uk.co.whitbread.business.tether.exception.InValidTokenException;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import uk.co.whitbread.business.tether.utils.WorldlineUtils;
import uk.co.whitbread.business.tether.validation.WorldLineTetherResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumberResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumberResponse;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static uk.co.whitbread.business.tether.exception.ErrorCodes.INVALID_ACCOUNT_OR_CARD_VAL;


@Service
@Slf4j
@AllArgsConstructor
public class BusinessTetherService {
    private static final int CARD_NUMBER_LENGTH = 19;
    private static final int ACCOUNT_NUMBER_LENGTH = 16;
    private static final String ACCOUNT_NUMBER_PREFIX = "3089";
    private static final String PIBA_EURO_ACC_NUM_PREFIX = "63562902";
    private static final String INVALID_ACCOUNT_OR_CARD = "Link ID contains invalid account number or card number";

    private final WebServiceTemplate webServiceTemplate;
    private final WorldLineProperties worldLineProperties;
    private final WorldLineWebServiceMessageCallback loginWebServiceMessageCallback;
    private final WorldlineTransformer worldlineBusinessTetherTransformer;
    private final WorldLineTetherResponseValidator worldLineTetherResponseValidator;
    private final PibaGuidServiceClient pibaGuidServiceClient;
    private final TokenService authTokenService;
    private final SchemeExtractor schemeExtractor;
    private final CdhRegistrationService cdhRegistrationService;
    private final WorldlineUtils worldlineUtils;

    public String tetherByAccountOrCardRequest(TetherLinkRequest tetherLinkRequest, String authorization) {
        String sanitizedLinkId = tetherLinkRequest.getLinkId().replace("\n", "").replace("\r", "");
        log.info("Called BusinessTetherService.tetherByAccountOrCardRequest. linkId={}, saveInCdh={}",
            sanitizedLinkId, tetherLinkRequest.isSaveInCdh());

        String tetherGuid;
        EmployeeDetails employeeDetails = getEmployeeDetails(authorization);
        try {
            Scheme scheme = schemeExtractor.extractScheme(tetherLinkRequest);
            if (isTetherByAccount(tetherLinkRequest.getLinkId())) {
                log.info("Called BusinessTetherService Tether By Account. linkId={}", sanitizedLinkId);
                tetherGuid = tetherByAccount(tetherLinkRequest, scheme);
            } else {
                log.info("Called BusinessTetherService Tether By Card. linkId={}", sanitizedLinkId);
                tetherGuid = tetherByCard(tetherLinkRequest, scheme);
            }
            if (tetherLinkRequest.isSaveInCdh()) {
                CdhEmployeeDetails cdhEmployeeDetails =
                    authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
                saveTetherInformationInCdh(tetherGuid, employeeDetails, scheme, cdhEmployeeDetails.getUserEmail());
            } else {
                saveTetherInformation(tetherGuid, employeeDetails, scheme);
            }
        } catch (SoapFaultClientException e) {
            log.error("WorldLine tether with account/card failed with the following error: ", e);
            throw new BusinessTetherException(e.getMessage());
        }

        return tetherGuid;
    }

    public EmployeeDetails getEmployeeDetails(String authorization) {
        EmployeeDetails employeeDetails;
        if (!authorization.isEmpty()) {
            employeeDetails = authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization);
            if (employeeDetails == null || isNull(employeeDetails.getEmployeeId()) || isNull(
                    employeeDetails.getCompanyId())) {
                throw new InValidTokenException(
                        "Error while trying to retrieve company/employee details from Auth0 token");
            } else {
                log.info("Called companyId = {}", employeeDetails.getCompanyId());
                log.info("Called employeeId = {}", employeeDetails.getEmployeeId());
            }
        } else {
            throw new InValidTokenException("Error while trying to retrieve token from Auth0");
        }
        return employeeDetails;
    }

    public void saveTetherInformation(String tetherGuid, EmployeeDetails employeeDetails, Scheme scheme) {
            ofNullable(tetherGuid)
                    .map(tetherGuidId -> worldlineBusinessTetherTransformer
                            .toPibaTetheredGuidRequest(tetherGuid, employeeDetails, scheme))
                    .ifPresent(pibaGuidServiceClient::savePibaGuid);
    }

    private boolean isTetherByAccount(String linkId) {
        if (linkId.length() == ACCOUNT_NUMBER_LENGTH && (linkId.startsWith(ACCOUNT_NUMBER_PREFIX) || linkId.startsWith(PIBA_EURO_ACC_NUM_PREFIX))) {
            return true;
        }
        if (linkId.length() == CARD_NUMBER_LENGTH && (linkId.startsWith(ACCOUNT_NUMBER_PREFIX) || linkId.startsWith(PIBA_EURO_ACC_NUM_PREFIX))) {
            return false;
        }
        throw new BusinessTetherException(INVALID_ACCOUNT_OR_CARD)
                .withErrorCode(INVALID_ACCOUNT_OR_CARD_VAL.getCode());
    }


    private String tetherByCard(TetherLinkRequest tetherLinkRequest, Scheme scheme) {
        TetherByCardNumberResponse response = (TetherByCardNumberResponse) dispatchWorldLineRequest(
                    worldlineBusinessTetherTransformer.toTetherByCardRequest(tetherLinkRequest, scheme));
        worldLineTetherResponseValidator.validate(response);
        return response.getResponse().getTetherDetails().getTetheredUserGuid();
    }


    private String tetherByAccount(TetherLinkRequest tetherLinkRequest, Scheme scheme) {
        TetherByAccountNumberResponse response = (TetherByAccountNumberResponse) dispatchWorldLineRequest(
                    worldlineBusinessTetherTransformer.toTetherByAccountRequest(tetherLinkRequest, scheme));

        worldLineTetherResponseValidator.validate(response);
        return response.getResponse().getTetherDetails().getTetheredUserGuid();
    }

    private void saveTetherInformationInCdh(String tetherGuid, EmployeeDetails employeeDetails, Scheme scheme, String accessedBy) {
        cdhRegistrationService.registerTetheredGuids(tetherGuid, employeeDetails, scheme, accessedBy);
    }

    private <T> Object dispatchWorldLineRequest(T requestObject) {
        log.info("Sending WorldlineRequest for {} = {} #####",
            requestObject.getClass().getSimpleName(), worldlineUtils.serializeObject(requestObject));
        var response = webServiceTemplate.marshalSendAndReceive(
            worldLineProperties.getPiba().getService().getUrl(),
            requestObject,
            loginWebServiceMessageCallback
        );
        log.info("Received WorldlineResponse for {} = {} #####",
            response.getClass().getSimpleName(), worldlineUtils.serializeObject(response));
        return response;
    }
}
