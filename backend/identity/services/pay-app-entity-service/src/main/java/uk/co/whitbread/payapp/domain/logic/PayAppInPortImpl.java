package uk.co.whitbread.payapp.domain.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardRequest;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.AppPreCheckRequest;
import uk.co.whitbread.payapp.domain.model.in.ApplicationDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.DeleteCardRequest;
import uk.co.whitbread.payapp.domain.model.in.DeletePayApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppCardsRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppLookupRequest;
import uk.co.whitbread.payapp.domain.model.in.GetDdSepaFormStatusRequest;
import uk.co.whitbread.payapp.domain.model.in.GetUserPreferencesRequest;
import uk.co.whitbread.payapp.domain.model.in.InitializeApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.RemoveParticipantRequest;
import uk.co.whitbread.payapp.domain.model.in.ShareAppRequest;
import uk.co.whitbread.payapp.domain.model.in.SubmitApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppContactDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateResumeUrlRequest;
import uk.co.whitbread.payapp.domain.model.in.jwt.JwtTokenClaims;
import uk.co.whitbread.payapp.domain.model.out.AddApplicationCardResponse;
import uk.co.whitbread.payapp.domain.model.out.AppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.AppPreCheckResponse;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.DeletePayApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.DirectDebitResponse;
import uk.co.whitbread.payapp.domain.model.out.FetchApplicationsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetAppCardsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetDdSepaFormStatusResponse;
import uk.co.whitbread.payapp.domain.model.out.GetUserPreferencesResponse;
import uk.co.whitbread.payapp.domain.model.out.InitializeApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.ShareAppResponse;
import uk.co.whitbread.payapp.domain.model.out.SubmitApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppContactDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateResumeUrlResponse;
import uk.co.whitbread.payapp.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.payapp.domain.ports.primary.PayAppInPort;
import uk.co.whitbread.payapp.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.payapp.domain.ports.secondary.PayAppOutPort;

@Slf4j
@RequiredArgsConstructor
public class PayAppInPortImpl implements PayAppInPort {

  private final PayAppOutPort payAppOutPort;
  private final CdhOutPort cdhOutPort;

  @Override
  public FetchApplicationsResponse fetchApplicationsDetails(final JwtTokenClaims jwtTokenClaims,
      final String clientIp) {
    return payAppOutPort.fetchApplicationsDetails(jwtTokenClaims, clientIp);
  }

  @Override
  public InitializeApplicationResponse initializeApplication(
      final InitializeApplicationRequest initializeApplicationRequest,
      String clientIp) {
    return payAppOutPort.initializeApplication(initializeApplicationRequest, clientIp);
  }

  @Override
  public UpdateAppContactDetailsResponse updateAppContactDetails(
      UpdateAppContactDetailsRequest request,
      JwtTokenClaims jwtTokenClaims,
      String clientIp) {
    return payAppOutPort.updateAppContactDetails(request, jwtTokenClaims, clientIp);
  }

  @Override
  public UpdateAppCompanyDetailsResponse updateAppCompanyDetails(
      UpdateAppCompanyDetailsRequest request,
      String clientIp) {
    return payAppOutPort.updateAppCompanyDetails(request, clientIp);
  }

  @Override
  public DeletePayApplicationResponse deleteApplication(
      DeletePayApplicationRequest request,
      JwtTokenClaims jwtTokenClaims,
      String clientIp) {
    return payAppOutPort.deleteApplication(request, jwtTokenClaims, clientIp);
  }

  @Override
  public Map<String, List<String>> getAppLookup(
      GetAppLookupRequest request,
      String clientIp) {
    return payAppOutPort.getAppLookup(request, clientIp);
  }

  @Override
  public AppCompanyDetailsResponse lookupCompanyDetails(
      AppCompanyDetailsRequest companyDetailsRequest, String clientIp) {
    return payAppOutPort.lookupCompanyDetails(companyDetailsRequest, clientIp);
  }

  @Override
  public List<GetUserPreferencesResponse> getUserPreferences(
      GetUserPreferencesRequest userPreferencesRequest, JwtTokenClaims jwtTokenClaims,
      String clientIp) {

    var requestTetheredGuids = validateAndFilterTetheredGuids(userPreferencesRequest,
        jwtTokenClaims);
    return payAppOutPort.getUserPreferences(requestTetheredGuids, clientIp);
  }

  @Override
  public ApplicationDetailsResponse applicationDetails(
      ApplicationDetailsRequest applicationDetailsRequest, JwtTokenClaims jwtTokenClaims,
      String clientIp) {
    return payAppOutPort.applicationDetails(applicationDetailsRequest, jwtTokenClaims, clientIp);
  }

  @Override
  public ShareAppResponse shareApp(ShareAppRequest shareAppRequest) {
    return payAppOutPort.shareApp(shareAppRequest);
  }

  @Override
  public void deleteApplicationCard(DeleteCardRequest request, JwtTokenClaims jwtTokenClaims,
      String clientIp) {
    payAppOutPort.deleteApplicationCard(request, jwtTokenClaims, clientIp);
  }

  @Override
  public AddApplicationCardResponse addApplicationCard(
      AddApplicationCardRequest addApplicationCardRequest,
      String clientIp) {
    return payAppOutPort.addApplicationCard(addApplicationCardRequest, clientIp);
  }

  private Set<String> validateAndFilterTetheredGuids(
      GetUserPreferencesRequest userPreferencesRequest,
      JwtTokenClaims jwtTokenClaims) {

    var companyId = jwtTokenClaims.getCompanyId();
    var employeeId = jwtTokenClaims.getEmployeeId();
    var tetheredGuidsResponse = cdhOutPort
        .getTetheredGuids(companyId, employeeId, jwtTokenClaims.getEmail());
    var tetheredGuids = tetheredGuidsResponse.stream().map(TetheredGuidResponse::getTetheredGuid)
        .collect(Collectors.toSet());

    var requestTetheredGuids = new HashSet<>(userPreferencesRequest.getTetheredUserGuids());

    var requestTetheredGuidsIterator = requestTetheredGuids.iterator();
    while (requestTetheredGuidsIterator.hasNext()) {
      var requestTetheredGuid = requestTetheredGuidsIterator.next();
      if (!tetheredGuids.contains(requestTetheredGuid)) {
        log.error(String.format(
            "User identified with companyId=%s and employeeId=%s does not have associated a tetheredUserGuid=%s",
            companyId, employeeId, requestTetheredGuid));
        requestTetheredGuidsIterator.remove();
      }
    }
    return requestTetheredGuids;
  }

  @Override
  public GetAppCardsResponse getApplicationCards(GetAppCardsRequest getAppCardsRequest,
      String clientIp) {
    return payAppOutPort.getApplicationCards(getAppCardsRequest, clientIp);
  }

  @Override
  public void removeParticipant(RemoveParticipantRequest removeParticipantRequest) {
    payAppOutPort.removeParticipant(removeParticipantRequest);
  }

  @Override
  public SubmitApplicationResponse submitApplication(
      SubmitApplicationRequest submitApplicationRequest, String clientIp) {
    return payAppOutPort.submitApplication(submitApplicationRequest, clientIp);
  }

  @Override
  public AppPreCheckResponse appPreCheck(AppPreCheckRequest appPreCheckRequest, String clientIp) {
    return payAppOutPort.appPreCheck(appPreCheckRequest, clientIp);
  }

  @Override
  public DirectDebitResponse directDebit(DirectDebitRequest directDebitRequest, String clientIp) {
    return payAppOutPort.directDebit(directDebitRequest, clientIp);
  }

  @Override
  public UpdateResumeUrlResponse updateResumeUrl(UpdateResumeUrlRequest updateResumeUrlRequest) {
    return payAppOutPort.updateResumeUrl(updateResumeUrlRequest);
  }

  @Override
  public GetDdSepaFormStatusResponse getDdSepaFormStatus(
      GetDdSepaFormStatusRequest getDdSepaFormStatusRequest, String clientIp) {
    return payAppOutPort.getDdSepaFormStatus(getDdSepaFormStatusRequest, clientIp);
  }
}
