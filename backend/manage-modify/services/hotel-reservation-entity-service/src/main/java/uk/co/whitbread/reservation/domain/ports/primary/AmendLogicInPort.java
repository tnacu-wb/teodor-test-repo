package uk.co.whitbread.reservation.domain.ports.primary;

import java.time.LocalDate;
import java.util.List;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendConfirmationPricesRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendOnHoldInterval;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendPaymentPageRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendConfirmationPricesResponse;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendPaymentPageResponse;
import uk.co.whitbread.reservation.domain.model.amend.out.ConfirmAmendLogicResponse;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryDetails;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;

public interface AmendLogicInPort {

  boolean checkCreateOnHoldReservation(String arrivalDate, String departureDate,
                                       String newArrivalDate,
                                       String newDepartureDate);

  List<AmendOnHoldInterval> getAmendOnHoldReservationInterval(String arrivalDate,
                                                              String departureDate,
                                                              String newArrivalDate,
                                                              String newDepartureDate);

  LocalDate getLocalDateFromString(String stringDate);

  AmendOnHoldInterval createAmendOnHoldInterval(String arrivalDate,
                                                String departureDate);

  AmendSummaryDetails getAmendSummaryDetails(AmendSummaryRequest amendSummaryRequest);

  boolean isLeadGuestUpdated(ReservationByIdResponse original, UpdateReservationRequest temp);

  AmendSummaryAmountResponse getAmountFromRateInfo(BasketResponse basket);

  ConfirmAmendLogicResponse confirmAmendLogic(ConfirmAmendLogicRequest confirmAmendRequest);

  AmendConfirmationPricesResponse getAmendConfirmationPrices(
      AmendConfirmationPricesRequest amendConfirmationPricesRequest);

  ReservationByBasketRefResponse getAllReservationsJustByBasketReference(
      String basketReference);

  AmendPaymentPageResponse amendPaymentPage(AmendPaymentPageRequest amendPaymentPageRequest);

  AmendSummaryAmountResponse getAmountFromReservationRateInfo(BasketDto basket);
}
