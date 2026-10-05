package uk.co.whitbread.payapp.domain.ports.secondary;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

public interface PayAppOutPort {

  FetchApplicationsResponse fetchApplicationsDetails(JwtTokenClaims jwtTokenClaims,
      String clientIp);

  InitializeApplicationResponse initializeApplication(
      InitializeApplicationRequest initializeApplicationRequest, String clientIp);

  UpdateAppContactDetailsResponse updateAppContactDetails(
      UpdateAppContactDetailsRequest updateAppContactDetailsRequest, JwtTokenClaims jwtTokenClaims,
      String clientIp);

  UpdateAppCompanyDetailsResponse updateAppCompanyDetails(
      UpdateAppCompanyDetailsRequest updateAppCompanyDetailsRequest, String clientIp);

  DeletePayApplicationResponse deleteApplication(
      DeletePayApplicationRequest request, JwtTokenClaims jwtTokenClaims, String clientIp);

  Map<String, List<String>> getAppLookup(GetAppLookupRequest request, String clientIp);

  AppCompanyDetailsResponse lookupCompanyDetails(
      AppCompanyDetailsRequest companyDetailsRequest, String clientIp);

  List<GetUserPreferencesResponse> getUserPreferences(Set<String> tetheredGuids,
      String clientIp);

  ApplicationDetailsResponse applicationDetails(ApplicationDetailsRequest applicationDetailsRequest,
      JwtTokenClaims jwtTokenClaims, String clientIp);

  AddApplicationCardResponse addApplicationCard(AddApplicationCardRequest addApplicationCardRequest,
       String clientIp);

  ShareAppResponse shareApp(ShareAppRequest shareAppRequest);

  void deleteApplicationCard(DeleteCardRequest request, JwtTokenClaims jwtTokenClaims,
      String clientIp);

  GetAppCardsResponse getApplicationCards(GetAppCardsRequest getAppCardsRequest, String clientIp);

  void removeParticipant(RemoveParticipantRequest removeParticipantRequest);

  SubmitApplicationResponse submitApplication(SubmitApplicationRequest submitApplicationRequest,
      String clientIp);

  AppPreCheckResponse appPreCheck(AppPreCheckRequest appPreCheckRequest, String clientIp);

  DirectDebitResponse directDebit(DirectDebitRequest directDebitRequest, String clientIp);

  UpdateResumeUrlResponse updateResumeUrl(UpdateResumeUrlRequest updateResumeUrlRequest);

  GetDdSepaFormStatusResponse getDdSepaFormStatus(
      GetDdSepaFormStatusRequest getDdSepaFormStatusRequest, String clientIp);
}
