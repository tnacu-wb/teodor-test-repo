package uk.co.whitbread.reservation.domain.ports.secondary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnpRequest;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendOnReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CopyReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.PackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationProfiles;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.UpdateBookerEmailRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.DonationPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.PackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.RatePlansResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.SaveReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;

public interface HotelReservationOhipOutPort {

  OhipReservationResponse createReservation(String hotelId,
      ReservationRequest createReservationRequest, String basketReference);

  ReservationsDetailsResponse getReservationsByBasketReference(String hotelId,
      String basketReference, int limit, int offset);

  ReservationsPackagesResponse getReservationsPackagesByBasketRef(String hotelId,
      String basketReferenceId, boolean mealInclusiveRate);

  ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest);

  ReservationByBasketRefResponse getReservationsByIds(String hotelId, List<String> reservationsIds,
                                                      Boolean priceBreakdownNeeded);

  ReservationByBasketRefResponse getReservationsByIds(String hotelId, List<String> reservationsIds,
                                                      Boolean priceBreakdownNeeded, Boolean operaUiCreated);

  ReservationByBasketRefResponse getReservationsByIds(String hotelId, List<String> reservationsIds,
      Boolean priceBreakdownNeeded, Boolean operaUiCreated, Boolean rateInfoNeeded);

  SaveReservationResponse updateReservationPackages(
      ReservationPackagesRequest reservationPackagesRequest);

  SaveReservationResponse updateReservationPackagesByReservationId(
      UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest);

  SaveReservationResponse updateReservationRateCode(UpdateRequest updateRateCodeRequest);

  SaveReservationResponse updateRoomType(UpdateRequest updateRoomTypeRequest);

  void createReservationGuest(ReservationGuestRequest guestReservationRequest);

  CancelInformationResponse getCancelInformation(String hotelId, Set<String> reservationsIds,
      String userDateTime);

  CancelReservationResponse cancelReservation(CancelReservationRequest cancelReservationRequest,
      List<DepositFoliosResponse> prepaidDeposits);

  HotelInformationResponse getHotelInformation(String hotelId);

  ReservationsDetailsEnhancedResponse getReservationsByExternalId(String resNo);

  ReservationByIdDetailsResponse getReservationsByReservationId(String resId, String hotelId);

  HotelAvailabilityByIdsV2 getHotelAvailabilityV2(
          HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsRequestOhipV2Dto);

  HotelAvailabilityByIds getHotelAvailability(
          HotelAvailabilityByIdsRequest request);

  void updateDiscount(UpdateDiscountRequest updateDiscountRequest);


  void updateCompanyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetailsRequest
      companyQuestionAndAnswerDetailsRequest);

  void updateBusinessItems(BusinessItemsRequest businessItemsRequest);

  void updateSpecialRequests(SpecialRequests specialRequestsEntity);

  SearchBookingsResponse searchBookings(SearchBookingsRequest searchBookingsRequest);

  void updateReasonForStay(UpdateReasonForStayRequest updateReasonForStayRequest);

  void updateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest);

  void updateReservationCcAgentId(
      UpdateReservationCcAgentIdRequest updateReservationCcAgentIdRequest);

  DepositsResponse getDepositsForReservationId(String hotelId, String reservationId);

  CancellationPoliciesResponse getCancellationPolicies(
      Set<String> reservationIds, String hotelId, String rateCode, String arrivalDate);

  MarketingPreferencesResponse getMarketingPreferences(String hotelId, String reservationId);

  CopyReservationsResponse copyReservations(CopyReservationsRequest copyReservationsRequest);

  AmendSummaryAmountResponse getAmendSummaryDetails(
      AmendSummaryAmountRequest amendSummaryAmountRequest);

  BookingAllowancesResponse getBookingAllowances(String hotelId, String reservationId,
      List<String> basketBookingAllowances);

  RatePlansResponse getRatePlans(List<String> ratePlanCodes, String hotelId);

  void amendEditRoom(UpdateReservationsRequest updateReservationRequest);

  ReservationByBasketRefResponse confirmAmend(
      ConfirmAmendOnReservationsRequest confirmAmendOnReservationsRequest);

  ReservationByBasketRefResponse confirmAmendForSingleCall(
          AmendDistributionSingleCallRequest amendDistributionSingleCallRequest);

  void deleteReservation(String hotelId, String reservationId);

  void updateReservations(UpdateReservationsRequest updateReservationsRequest);

  void updateReservationsSingleCall(UpdateReservationsRequest updateReservationsRequest);

  List<String> getWbRoomTypes();

  ReservationsPackagesResponse getReservationsPackagesByIds(String hotelId,
      List<String> reservationIds);

  DonationPackagesResponse getCharityPackagesDetails(String hotelId,
      List<String> packageCodes);

  void movePaymentDetails(String hotelId, Set<String> reservationIds);

  DepositFoliosResponse getGeneratedDepositFolios(String hotelId, Set<String> reservationIds);

  void updateReservationBooker(BookerDetailsCnpRequest bookerDetailsCnpRequest);

  void updateBookerEmail(UpdateBookerEmailRequest bookerDetailsCnpRequest);

  void deleteRoutingInstructions(String hotelId, Set<String> reservationIds);

  MemosResponse createMemo(CreateMemoRequest createMemoRequest);

  MemosResponse getMemos(String hotelId, Set<String> reservationIds);

  void saveCharges(DepositFoliosResponse depositFolios);

  void attachProfileToReservations(AttachReservationProfileRequest attachReservationProfileRequest);

  PackagesResponse getPackages(PackagesRequest packagesRequest);

  ConfirmReservationResponse updateReservation(
      UpdateReservationSingleCallRequest updateReservationRequest);

  ReservationProfiles createProfiles(ReservationGuestRequest reservationGuestRequest);

  PreCheckInResponse addAttachmentToReservation(ReservationFileAttachmentRequest reservationFileAttachmentRequest);

  PreCheckInResponse saveReservationPreCheckIn(PreCheckInRequest preCheckInRequest);
  
  void updateReservationExternalReference(String hotelId, List<String> reservationIds,
      String externalReference);

  void deleteRegCardAttachment(String hotelId, String reservationId);

  void deleteReservationPreCheckIn(String hotelId, String reservationId);

  void updateReservationPackagesScheduled(
      ReservationPackagesScheduledRequest reservationPackagesRequest, String arrival,
      String departure);

  void linkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest);

  void updateReservationPreferences(ReservationPreferencesRequest reservationPreferencesRequest);

  void updateReservationAlerts(UpdateReservationAlertsRequest updateReservationAlertsRequest);

  void updateCancellationPolicies(
      UpdateCancellationPoliciesRequest updateCancellationPoliciesRequest);

  void updateUdfc20(UpdateReservationUdfsRequest updateReservationUdfsRequest);

  void saveDepositFolios(DepositFoliosRequest depositFoliosRequest);
}
