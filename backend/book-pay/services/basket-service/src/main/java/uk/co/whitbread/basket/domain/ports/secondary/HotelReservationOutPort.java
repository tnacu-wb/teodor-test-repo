package uk.co.whitbread.basket.domain.ports.secondary;

import java.util.List;
import java.util.Set;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationResponse;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationProfiles;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.payments.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.basket.domain.model.payments.in.DiscountRequest;
import uk.co.whitbread.basket.domain.model.reservation.in.DepositFoliosRequest;
import uk.co.whitbread.basket.domain.model.reservation.in.ReservationAlertsRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;

public interface HotelReservationOutPort {
  ReservationByBasketRefResponse getReservationsByBasketReference(String basketReference,
      String priceBreakDownNeeded, boolean rateInfoNeeded, Boolean useCache);

  ReservationByBasketRefResponse getReservationsByBasketReference(String basketReference,
      String priceBreakDownNeeded, boolean rateInfoNeeded);

  void updateDiscount(DiscountRequest discountRequest,
      List<String> reservationIds, String hotelId, String currencyCode);

  void updateBusinessItems(BusinessItems businessItems, List<String> reservationIds,
      String hotelId, String companyId, String channel);

  void updateCompanyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails,
      Set<String> reservationIds, String hotelId);

  void updateSpecialRequests(List<String> specialRequests, List<String> bookingNotes,
      List<String> reservationIds, String hotelId);

  DepositsResponse getDepositsForReservationId(String hotelId, String reservationId);

  MarketingPreferencesResponse getMarketingPreferences(String hotelId, String reservationId);

  void attachProfileToReservations(String hotelId, String profileId, Set<String> reservationIds);

  void deleteRoutingInstructions(String hotelId, Set<String> reservationIds);

  ConfirmReservationResponse updateReservationRequest(ReservationGuestRequest guestReservationRequest,
      ReservationPackagesRequest updatePackageReservationRequest, BusinessItems finalBusinessItems,
      List<String> specialRequests, List<String> bookingNotes, String hotelId,
      String reservationId, ConfirmReservationRequest confirmReservationRequest);

  ReservationProfiles createProfileIds(ReservationGuestRequest guestReservationRequest);

  void updateReservationAlerts(ReservationAlertsRequest reservationUdfRequest);

  /**
   * Retrieves preview deposit folios (proposed charges) for one or more reservations.
   *
   * @param hotelId       the hotel identifier
   * @param reservationIds set of reservation IDs to fetch preview deposits for
   * @return DepositFoliosResponse containing preview deposit details per reservation
   */
  DepositFoliosResponse getPreviewDepositsForReservationId(String hotelId, Set<String> reservationIds);

  /**
   * Saves deposit folios to the hotel reservation system.
   *
   * @param dto the deposit folios payload containing charges to save
   */
  void saveDepositFolios(DepositFoliosRequest dto);
}
