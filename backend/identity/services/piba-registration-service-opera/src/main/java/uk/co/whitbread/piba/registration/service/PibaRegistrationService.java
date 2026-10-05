package uk.co.whitbread.piba.registration.service;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.piba.registration.client.CdhClient;
import uk.co.whitbread.piba.registration.client.PibaAccountServiceClient;
import uk.co.whitbread.piba.registration.converter.WorldlineTransformer;
import uk.co.whitbread.piba.registration.exception.InValidTokenException;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationResponse;
import uk.co.whitbread.piba.registration.model.RegistrationInfoResponse;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitResp;
import uk.co.whitbread.piba.registration.model.Scheme;
import uk.co.whitbread.piba.registration.model.TetheredGuidDetails;
import uk.co.whitbread.piba.registration.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.registration.model.TetheredUserRequest;
import uk.co.whitbread.piba.registration.properties.CdhProperties;
import uk.co.whitbread.piba.registration.util.LogUtils;
import uk.co.whitbread.piba.registration.util.WorldlineUtils;
import uk.co.whitbread.piba.registration.validation.WorldLineRegistrationResponseValidator;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.CardHolder;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class PibaRegistrationService {

  private final WebServiceTemplate worldlineWebServiceTemplate;
  private final WorldlineTransformer worldlineRegistrationTransformer;
  private final WorldLineProperties worldLineProperties;
  private final WorldLineRegistrationResponseValidator worldLineResponseValidator;
  private final WorldLineWebServiceMessageCallback worldLineWebServiceMessageCallback;
  private final TokenService authTokenService;
  private final PibaGuidServiceClient pibaGuidServiceClient;
  private final CdhProperties cdhProperties;
  private final PibaAccountServiceClient pibaAccountServiceClient;
  private final CdhClient cdhClient;
  private final Executor pibaRegExecutor;
  private final WorldlineUtils worldlineUtils;
  private static final String COMPLETED = "Completed";

  public RegistrationInfoResponse getInfo(String registrationCode) throws SoapFaultClientException {
    RegistrationGetInfoResponse response = (RegistrationGetInfoResponse) dispatchWorldLineRequest(
        worldlineRegistrationTransformer.toRegistrationGetInfoSoapRequest(registrationCode));
    worldLineResponseValidator.validate(response);

    return worldlineRegistrationTransformer.toRegistrationInfoResponse(response);
  }

  public RegistrationAuthenticationResponse authenticate(
      RegistrationAuthenticationRequest registrationAuthenticationRequest)
      throws SoapFaultClientException {

    RegistrationAuthenticateResponse response =
        (RegistrationAuthenticateResponse) dispatchWorldLineRequest(
            worldlineRegistrationTransformer.toRegistrationAuthenticateSoapRequest(
                registrationAuthenticationRequest));

    worldLineResponseValidator.validate(response);

    return worldlineRegistrationTransformer.toRegistrationAuthResponse(response);
  }

  public RegistrationSubmitResp submit(RegistrationSubmitRequest registrationSubmitRequest,
      String authorization) throws SoapFaultClientException {
    log.info("Called PibaRegistrationService.submit with RegistrationCode={} and InnBusiness={}",
        LogUtils.sanitisedStringWithMaxLengthLimit(registrationSubmitRequest.getRegistrationCode(),
            50),
        registrationSubmitRequest.isInnBusiness());
    EmployeeDetails employeeDetails = registrationSubmitRequest.isInnBusiness() ?
        authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization)
        : getEmployeeDetails(authorization);

    RegistrationSubmitResponse response = (RegistrationSubmitResponse) dispatchWorldLineRequest(
            worldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(
                registrationSubmitRequest));
    worldLineResponseValidator.validate(response);
    Scheme scheme = worldlineRegistrationTransformer.extractScheme(
        registrationSubmitRequest.getRegistrationCode());

    if (registrationSubmitRequest.isInnBusiness()) {
      processForInnBusiness(response, authorization, scheme, employeeDetails);
    } else {
      saveTetherInformation(response.getResponse().getTetherDetails().getTetheredUserGuid(),
          employeeDetails, scheme);
    }

    var registrationSubmitResponse = worldlineRegistrationTransformer.toRegistrationSubmitResponse(
        response);

    if (registrationSubmitRequest.isInnBusiness()) {
      var tetheredUserDetails = fetchTetheredUserDetails(authorization,
          response.getResponse().getTetherDetails().getTetheredUserGuid(),scheme.toString());
      Optional.ofNullable(tetheredUserDetails)
          .map(t -> t.getCustomerAccountOverview().getAccountNumber())
          .ifPresent(registrationSubmitResponse.getTetherDetails()::setAccountNumber);
    }

    return registrationSubmitResponse;
  }

  private TetheredUserDetailsResponse fetchTetheredUserDetails(String authorization,
      String tetheredUserGuid, String scheme) {
    try {
      return pibaAccountServiceClient.getTetheredUserDetailsWithScheme(authorization, tetheredUserGuid, scheme);
    } catch (Exception e) {
      log.error("Failed to fetch tethered user details: {}", e.getMessage(), e);
      return null;
    }
  }

  private void processForInnBusiness(RegistrationSubmitResponse registrationSubmitResponse,
      String authorization, Scheme scheme, EmployeeDetails employeeDetails) {
    var cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
        authorization);

    registerTetheredUser(
        registrationSubmitResponse.getResponse().getTetherDetails().getTetheredUserGuid(),
        authorization, employeeDetails.getCompanyId(), employeeDetails.getEmployeeId(), scheme);
    ApplicationResponse applicationResponse = cdhClient.fetchApplication(
        null,
        registrationSubmitResponse.getResponse().getApplicationGuid(),
        cdhEmployeeDetails.getUserEmail());
    if (applicationResponse == null) {
      log.warn("Application not found in CDH for applicationGuid={}",
          registrationSubmitResponse.getResponse().getApplicationGuid());
    } else {
      linkApplicationCardHoldersToEmployee(employeeDetails, scheme,
          cdhEmployeeDetails.getUserEmail(), authorization, applicationResponse);
      if (shouldMarkAppCompleted(applicationResponse)) {
        markAppCompleted(applicationResponse.getApplicationId(), cdhEmployeeDetails.getUserEmail());
      }
    }
  }

  private boolean shouldMarkAppCompleted(
      ApplicationResponse application) {
    return application.getStage().equals("Outstanding") || application.getStage()
        .equals("Accepted");
  }

  private void markAppCompleted(String applicationId, String userEmail) {
    cdhClient.updateAppStatus(applicationId, COMPLETED, userEmail);
  }

  private void linkApplicationCardHoldersToEmployee(EmployeeDetails employeeDetails, Scheme scheme,
                                                    String userEmail, String authorization,
                                                    ApplicationResponse applicationResponse) {
    List<CardHolder> cardHolders = applicationResponse.getCardHolders();

    if (cardHolders == null || cardHolders.isEmpty()) {
      log.info("No card holders found to process.");
      return;
    }

    var futures = cardHolders.stream()
        .map(cardHolder ->
            CompletableFuture.runAsync(
                () -> processSingleCardHolder(cardHolder, userEmail, authorization, employeeDetails,
                    scheme),
                pibaRegExecutor
            )
        )
        .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

  }

  private void processSingleCardHolder(CardHolder cardHolder, String userEmail,
      String authorization, EmployeeDetails employeeDetails, Scheme scheme) {
    var tetheredGuids = cdhClient.getTetheredGuids(
        employeeDetails.getCompanyId(),
        String.valueOf(cardHolder.getEmployeeId()),
        userEmail
    );

    boolean isAlreadyTethered = tetheredGuids.stream()
        .anyMatch(response -> response.getTetheredGuid()
            .equals(cardHolder.getUserGuid()));

    if (isAlreadyTethered) {
      log.info(
          "Card already tethered with GUID={} for employeeId={}",
          cardHolder.getUserGuid(),
          cardHolder.getEmployeeId()
      );
    } else {
      log.info("Tethering card for employeeId={}", cardHolder.getEmployeeId());
      registerTetheredUser(
          cardHolder.getUserGuid(),
          authorization,
          employeeDetails.getCompanyId(),
          String.valueOf(cardHolder.getEmployeeId()),
          scheme
      );
    }
  }

  private void registerTetheredUser(String tetheredUserGuid, String authorization, String companyId,
      String employeeId,
      Scheme scheme) {
    log.info("Saving in CDH tethered user: tetherGuid {}, companyId {}, employeeId {}",
        tetheredUserGuid,
        companyId, employeeId);

    TetheredUserRequest request = TetheredUserRequest.builder()
        .scheme(scheme)
        .companyId(companyId)
        .tetheredGuids(List.of(
            TetheredGuidDetails.builder()
                .employeeId(employeeId)
                .tetheredGuid(tetheredUserGuid)
                .build()
        ))
        .build();
    pibaAccountServiceClient.registerTetheredUser(authorization, request);
  }

  public void saveTetherInformation(String tetherGuid, EmployeeDetails employeeDetails,
      Scheme scheme) {
    ofNullable(tetherGuid)
        .map(tetherGuidId -> worldlineRegistrationTransformer
            .toPibaTetheredGuidRequest(tetherGuid, employeeDetails, scheme))
        .ifPresent(pibaGuidServiceClient::savePibaGuid);
  }

  public EmployeeDetails getEmployeeDetails(String authorization) {
    EmployeeDetails employeeDetails;
    if (!authorization.isEmpty()) {
      if (cdhProperties.isEnableBbDataFetch()) {
        CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            authorization);
        log.info("CdhEmployeeDetails from authTokenService :: {}", cdhEmployeeDetails);
        employeeDetails = new EmployeeDetails();
        employeeDetails.setCompanyId(cdhEmployeeDetails.getCompanyAccountId());
        employeeDetails.setEmployeeId(cdhEmployeeDetails.getEmployeeAccountId());
      } else {
        employeeDetails = authTokenService.retrieveEmployeeDetailsAndVerifyToken(
            authorization);
        log.info("employeeDetails from authTokenService :: {}", employeeDetails);
      }
      validateEmployeeDetails(employeeDetails);
    } else {
      throw new InValidTokenException("Error while trying to retrieve token from Auth0");
    }
    return employeeDetails;
  }

  private void validateEmployeeDetails(EmployeeDetails employeeDetails) {
    if (employeeDetails == null || isNull(employeeDetails.getEmployeeId()) || isNull(
        employeeDetails.getCompanyId())) {
      log.error("Missing companyId or employeeId in auth0 token");
      throw new InValidTokenException(
          "Error while trying to retrieve company/employee details from Auth0 token");
    } else {
      log.info("Called companyId = {} and employeeId = {}", employeeDetails.getCompanyId(),
          employeeDetails.getEmployeeId());
    }
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
