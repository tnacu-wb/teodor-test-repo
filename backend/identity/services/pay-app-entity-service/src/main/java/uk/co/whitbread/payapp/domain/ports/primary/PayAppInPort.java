package uk.co.whitbread.payapp.domain.ports.primary;

import java.util.List;
import java.util.Map;
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

public interface PayAppInPort {

  InitializeApplicationResponse initializeApplication(
      InitializeApplicationRequest initializeApplicationRequest,
      String clientIp);

  FetchApplicationsResponse fetchApplicationsDetails(
      JwtTokenClaims jwtTokenClaims,
      String clientIp);

  UpdateAppContactDetailsResponse updateAppContactDetails(
      UpdateAppContactDetailsRequest request,
      JwtTokenClaims jwtTokenClaims,
      String clientIp);

  UpdateAppCompanyDetailsResponse updateAppCompanyDetails(
      UpdateAppCompanyDetailsRequest request,
      String clientIp);

  DeletePayApplicationResponse deleteApplication(
      DeletePayApplicationRequest request,
      JwtTokenClaims jwtTokenClaims, String clientIp);

  Map<String, List<String>> getAppLookup(
      GetAppLookupRequest request,
      String clientIp);

  AppCompanyDetailsResponse lookupCompanyDetails(AppCompanyDetailsRequest companyDetailsRequest,
      String clientIp);

  List<GetUserPreferencesResponse> getUserPreferences(
      GetUserPreferencesRequest userPreferencesRequest,
      JwtTokenClaims jwtTokenClaims, String clientIp);

  ApplicationDetailsResponse applicationDetails(ApplicationDetailsRequest applicationDetailsRequest,
      JwtTokenClaims jwtTokenClaims, String clientIp);

  AddApplicationCardResponse addApplicationCard(
      AddApplicationCardRequest addApplicationCardRequest, String clientIp);

  ShareAppResponse shareApp(ShareAppRequest shareAppRequest);

  void deleteApplicationCard(DeleteCardRequest request, JwtTokenClaims jwtTokenClaims, String clientIp);

  GetAppCardsResponse getApplicationCards(GetAppCardsRequest getAppCardsRequest, String clientIp);

  void removeParticipant(RemoveParticipantRequest removeParticipantRequest);

  SubmitApplicationResponse submitApplication(
      SubmitApplicationRequest submitApplicationRequest, String clientIp);

  AppPreCheckResponse appPreCheck(AppPreCheckRequest model, String clientIp);

  DirectDebitResponse directDebit(DirectDebitRequest directDebitRequest, String clientIp);

  UpdateResumeUrlResponse updateResumeUrl(UpdateResumeUrlRequest updateResumeUrlRequest);

  GetDdSepaFormStatusResponse getDdSepaFormStatus(
      GetDdSepaFormStatusRequest getDdSepaFormStatusRequest,
      String clientIp);
}
