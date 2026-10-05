package uk.co.whitbread.ohip.domain.ports.primary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmAmendForSingleRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmAmendOnReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CopyReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationProfiles;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.SpecialRequests;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateBookerEmailRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPolicyRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReasonForStayRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequestSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.BookingAllowancesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelInformationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancellationPoliciesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.PreCheckInResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetailsEnhancedResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.UpdateReasonForStayResponse;

public interface HotelReservationInPort {

  ReservationResponse processCreateReservation(ReservationRequest createReservationRequest);

  ReservationsDetailsResponse getReservationsByExternalReferenceIds(String hotelId,
      List<String> externalReferenceIds, int limit, int offset);

  List<ReservationsPaymentCardType> getReservationMethodPayment(String hotelId,
      List<String> reservationIds);

  ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest);

  ReservationByBasketRefResponse getReservationsByIds(String hotelId, Set<String> reservationIds,
                                                      Boolean priceBreakdownNeeded, boolean rateInfoNeeded,
                                                      Boolean operaUiCreatedRsv);

  ReservationLightweightResponse getReservationsByIds(String hotelId, Set<String> reservationIds);

  void updateReservationPackages(ReservationPackagesRequest reservationPackagesRequest);

  void updateDiscount(UpdateDiscountRequest updateDiscountRequest);

  void updateCompanyQuestionAndAnswerDetails(
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest);

  void updateBusinessItems(BusinessItemsRequest businessItemsRequest);

  void updateCustomReferenceNumber(UpdateCustomReferenceNumberRequest updateCustomReferenceNumberRequest);

  void updateSpecialRequests(SpecialRequests specialRequests);

  ReservationGuestResponse createReservationGuest(ReservationGuestRequest guestReservationRequest);

  void updateBillingAddress(BillingAddressRequest billingAddressRequest);

  void changeReservationRatePlan(RatePlanRoomTypeChangeRequest reservation);

  void changeReservationRoomType(RatePlanRoomTypeChangeRequest reservation);

  ReservationPackagesResponse getReservationsPackagesByReservationsIds(String hotelId,
      Set<String> reservationIds);

  ReservationPackagesResponse getReservationsPackagesMealInclusiveRateByReservationsIds(String hotelId,
      Set<String> reservationIds);

  CancelReservationResponse cancelReservation(
      CancelReservationRequest cancelReservationRequest);

  CancelInformationResponse getCancelInformation(String hotelId, Set<String> reservationIds,
      String userDateTime);

  ReservationDetailsEnhancedResponse getReservationsByExternalRefId(String externalReferenceId);

  ReservationIdDetailsResponse getReservationsByReservationId(String hotelId, String reservationId);

  SearchBookingsResponse searchBookings(BookingSearchCriteria bookingSearchCriteria);

  UpdateReasonForStayResponse updateReasonForStay(UpdateReasonForStayRequest updateReasonForStayRequest);

  void updateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest);

  void updateReservationCcAgentId(UpdateReservationCcAgentIdRequest updateReservationCcAgentId);

  DepositsResponse getDepositsForReservationId(final String hotelId, final String reservationId);

  CancellationPoliciesResponse getCancellationPolicies(
      Set<String> reservationIds, String hotelId, String rateCode, String arrivalDate);

  void updateCancellationPolicy(UpdateCancellationPolicyRequest updateCancellationPolicyRequest);

  void updateCancellationPolicies(
      UpdateCancellationPoliciesRequest updateCancellationPoliciesRequest);

  MarketingPreferencesResponse getMarketingPreferences(final String hotelId, final String reservationId);

  CopyReservationsResponse copyReservations(CopyReservationsRequest copyReservationsRequest);

  BookingAllowancesResponse getBookingAllowances(String hotelId, String reservationId,
      List<String> basketBookingAllowances);

  void deleteReservation(String hotelId, String reservationId);

  void updateReservations(UpdateReservationsRequest updateReservationsRequest);

  void updateReservationsWithExternalRef(String hotelId, Set<String> reservationIds, String externalReference);

  List<ChangeReservation> updateStayDateOrEditRoom(
          UpdateReservationsRequestSingleCall confirmAmendForSingleRequest);

  void updateRoutingInstructionsWithPayeeInfo(String hotelId, Set<String> reservationIds);

  ReservationByBasketRefResponse confirmAmend(ConfirmAmendOnReservationsRequest confirmAmendOnReservationsRequest);

  ReservationByBasketRefResponse confirmAmendForSingleCall(ConfirmAmendForSingleRequest confirmAmendForSingleRequest);

  void updateReservationsToPayOnArrival(String hotelId, Set<String> reservationIds);

  void movePaymentDetails(String hotelId, Set<String> reservationIds);

  void updateBookersDetails(BookerDetailsCnpRequest bookerDetailsCnpRequest);

  void updateBookerEmail(UpdateBookerEmailRequest updateBookerEmailRequest);

  void deleteRoutingInstruction(String hotelId, Set<String> reservationIds);

  MemosResponse createMemo(CreateMemoRequest createMemoRequest);

  MemosResponse getMemos(String hotelId, Set<String> reservationIds);

  void attachProfileToReservations(AttachReservationProfileRequest attachReservationProfileRequest);

  ConfirmReservationResponse updateReservationSingleCall(BusinessItemsRequest businessItemsRequest,
      SpecialRequests specialRequests, ReservationGuestRequest guestReservationRequest,
      ReservationPackagesRequest updateReservationRequest,
      ConfirmReservationRequest confirmReservationRequest);

  ReservationProfiles createProfiles(ReservationGuestRequest guestReservationRequest);

  PreCheckInResponse addAttachmentToReservation(
      ReservationFileAttachmentRequest reservationFileAttachmentRequest);

  PreCheckInResponse saveReservationPreCheckIn(PreCheckInRequest preCheckInRequest);

  void deleteReservationPreCheckIn(String hotelId, String reservationId);

  void deleteRegCardAttachment(String hotelId, String reservationId);

  void linkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest);

  void updateReservationPreferences(ReservationPreferencesRequest reservationPreferencesRequest);

  void updateReservationAlerts(UpdateReservationAlertsRequest updateUdf);

  ReservationAmounts getReservationAmounts(String hotelId, Set<String> reservationIds);

  PreCheckInResponse saveReservationPreRegister(PreCheckInRequest preCheckInRequest);
}

