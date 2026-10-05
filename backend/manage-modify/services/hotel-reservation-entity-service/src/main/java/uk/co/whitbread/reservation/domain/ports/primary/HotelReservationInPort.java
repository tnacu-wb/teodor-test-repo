package uk.co.whitbread.reservation.domain.ports.primary;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnp;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CopyBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationProfiles;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.UpdateCnpReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateEmailReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdatedReservationsDistribution;
import uk.co.whitbread.reservation.domain.model.out.AmendStayDatesResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationGuestResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.SaveReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.TempBookingRefResponse;
import uk.co.whitbread.reservation.domain.model.out.UpdateCnpReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.UpdateReasonForStayResponse;
import uk.co.whitbread.reservation.domain.model.out.UpdateReservationOverrideReasonsResponse;

public interface HotelReservationInPort {

  ReservationResponse createReservation(ReservationRequest createReservationRequest);

  ReservationsDetailsResponse getReservationsByBasketReference(
      String hotelId, String basketReference, int limit, int offset);

  ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest,
      Optional<DepositFoliosResponse> optionalDepositFoliosResponse);

  ReservationByBasketRefResponse getAllReservationsJustByBasketReference(String basketReference,
                                                                         Boolean priceBreakdownNeeded);

  ReservationsPackagesResponse getReservationsPackagesByBasketRef(
      String hotelId, String basketReferenceId, boolean mealInclusiveRate);

  SaveReservationResponse updateReservationPackages(
      ReservationPackagesRequest reservationPackagesRequest);

  SaveReservationResponse updateReservationPackagesById(
      UpdateReservationPackagesByIdRequest reservationPackagesByIdRequest, boolean isForAmend);

  UpdateReservationPackagesByIdRequest buildUpdateReservationPackagesByIdSingleCall(
          UpdateReservationPackagesByIdRequest reservationPackagesByIdRequest);

  SaveReservationResponse updateReservationRateCode(
      UpdateRequest updateRateCodeRequest);

  SaveReservationResponse updateRoomType(
      UpdateRequest updateRoomTypeRequest);

  ReservationGuestResponse createReservationGuest(
      String basketReference, ReservationGuestRequest guestReservationRequest);

  CancelReservationResponse cancelReservation(CancelReservationRequest cancelReservationRequest);

  CancelReservationResponse cancelOnHoldReservation(
      CancelReservationRequest cancelOnHoldReservationRequest);

  void updateDiscount(UpdateDiscountRequest updateDiscountRequest);

  void updateCompanyQuestionAndAnswerDetails(
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest);

  void updateBusinessItems(BusinessItemsRequest businessItemsRequest);

  void updateReservationSpecialRequests(SpecialRequests specialRequestsEntity);

  CancelReservationResponse rollbackReservation(CancelReservationRequest cancelReservationRequest);

  UpdateReasonForStayResponse updateReasonForStay(
      UpdateReasonForStayRequest updateReasonForStayRequest);

  UpdateReservationOverrideReasonsResponse updateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest);

  UpdateCnpReservationResponse updateCnpReservation(
      String basketRef, UpdateCnpReservationRequest updateCnpReservationRequest);

  DepositsResponse getDepositsForReservationId(String hotelId, String reservationId);

  CancellationPoliciesResponse getCancellationPolicies(
      String basketReference, String hotelId, String rateCode, String arrivalDate);

  MarketingPreferencesResponse getMarketingPreferences(String hotelId, String reservationId);

  TempBookingRefResponse addNewRoomToExistingBasket(String tempBookingRef,
      ReservationRequest addNewRoomRequest,
      ReservationByBasketRefResponse reservationByBasketRefResponse);

  AmendStayDatesResponse amendStayDates(AmendStayDatesRequest amendStayDatesRequest,
      Boolean isNonRefundable);

  UpdateReservationsRequest buildRequestAmendStayDatesSingleCall(
      List<AmendDistributionStayDatesRequest> amendStayDatesRequests,
          Boolean isNonRefundable);

  CopyBookingResponse copyBooking(CopyBookingRequest copyBookingRequest);

  BookingAllowancesResponse getBookingAllowances(String basketReference);

  TempBookingRefResponse editRoom(UpdateReservationsRequest updateReservationsRequest,
      String tempBasketRef,
      BookingChannel bookingChannel, Boolean isNonRefundable, Boolean isOta);

  UpdateReservationsRequest editRoomSingleCall(
          UpdateReservationsRequest updateReservationsRequest,
          String originalBasketRef,
          BookingChannel bookingChannel,
          Boolean isNonRefundable,
          Boolean isOta);

  ReservationByBasketRefResponse confirmAmend(ConfirmAmendRequest confirmAmendRequest, boolean isRatePlanCode);

  TempBookingRefResponse removeRoom(String tempBookingRef, String reservationId, String token,
      boolean checkLastRoom, BookingChannel bookingChannel, Boolean isNonRefundable);

  ReservationByBasketRefResponse getAllReservationsJustByBookingReferenceAuthenticated(
      String bookingReference, boolean isUserAuthenticated);

  DepositFoliosResponse getGeneratedDepositFolios(String hotelId, Set<String> reservationIds);

  ReservationByBasketRefResponse amendDistribution(
      String basketReference,
      ReservationRequest reservationRequest,
      UpdatedReservationsDistribution updateReservationsRequest,
      UpdateReservationPackagesByIdRequest reservationPackagesRequest,
      BookerDetailsCnp bookerDetailsCnp);


  void deleteRoutingInstructions(String hotelId, Set<String> reservationIds);

  void updateEmailReservation(String basketRef,
      UpdateEmailReservationRequest updateEmailReservationRequest);

  MemosResponse createMemo(CreateMemoRequest createMemoRequest);

  MemosResponse getMemos(String basketReference);

  void attachProfileToReservations(AttachReservationProfileRequest attachReservationProfileRequest);

  void deleteRoutingInstruction(String hotelId, Set<String> reservationIds);

  ConfirmReservationResponse updateReservation(
      UpdateReservationSingleCallRequest updateReservationRequest);

  ReservationProfiles createProfiles(ReservationGuestRequest reservationGuestRequest);

  PreCheckInResponse addAttachmentToReservation(ReservationFileAttachmentRequest reservationFileAttachmentRequest);

  PreCheckInResponse saveReservationPreCheckIn(PreCheckInRequest preCheckInRequest);

  SaveReservationResponse updateReservationPackageScheduled(
      ReservationPackagesScheduledRequest reservationPackagesScheduledRequest);

  void linkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest);

  void updateReservationPreferences(ReservationPreferencesRequest reservationPreferencesRequest);

  void updateReservationAlerts(UpdateReservationAlertsRequest updateReservationsRequest);

  void saveDepositFolios(DepositFoliosRequest depositFoliosRequest);
}
