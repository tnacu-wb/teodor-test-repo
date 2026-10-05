package uk.co.whitbread.basket.domain.ports.primary;

import java.util.List;
import java.util.Optional;
import uk.co.whitbread.basket.domain.model.basket.in.AddAmendedReservationsRequest;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemRequest;
import uk.co.whitbread.basket.domain.model.basket.in.CancelBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ConfirmItemProcessingRequest;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposits;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.domain.model.basket.in.PromotionsInformationRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateAllowancesRequest;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateBasketItemSupplement;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatusResponse;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;

public interface BasketInPort {

  Basket createBasket(final CreateBasketRequest createBasketRequest);

  Optional<Basket> getBasketByReference(final String reference);

  Basket getBasketById(String basketId);

  Basket addBasketItem(final AddBasketItemRequest addBasketItemRequest,
      final String requestModifyTimestamp);

  Basket updateAllowances(final String basketReference,
      final UpdateAllowancesRequest updateAllowancesRequest,
      final String requestModifyTimestamp);

  Basket linkAmendReservations(final String basketReference,
      final AddAmendedReservationsRequest addBasketItemRequest,
      final String requestModifyTimestamp);

  Basket removeBasketItems(final String reference, final List<String> itemIds,
      final String requestModifyTimestamp);

  void confirmItemProcessing(final String basketReference, final String itemId,
      final ConfirmItemProcessingRequest confirmItemProcessingRequest);

  void confirmRefundProcessing(final String basketReference,
      final ConfirmItemProcessingRequest confirmItemProcessingRequest);

  void deleteBasket(final String reference, final String requestModifyTimestamp);

  void cancelBasket(final CancelBasketRequest cancelBasketRequest);

  void sendEmailNotificationOption(final String reference, final Boolean sendMail);

  void saveCharges(final PrepaidDepositsRequest request);

  PrepaidDeposits getCharges(final String reservationId);

  PrepaidDeposits getCharges(List<String> reservationIds);

  BasketStatusResponse checkBasketStatus(final String basketReference);

  void confirmAmendProcessing(final String basketId,
      final ConfirmItemProcessingRequest confirmItemProcessingRequest);

  void confirmChangePaymentProcessing(final String basketId,
      final ConfirmItemProcessingRequest confirmItemProcessingRequest);

  void setErroredBooking(final String reference, final Boolean isErroredBooking,
      final BasketError basketError);

  void setPreAuthCharges(final String reference, final String preAuthCharges);

  ReservationByBasketRefResponse updateReservation(String basketReference, String hotelId, String requestId,
      ReservationGuestRequest guestReservationRequest,
      ReservationPackagesRequest updatePackageReservationRequest, PaymentRequest paymentRequest,
      String priceBreakDownNeeded);

  Basket updateBasketItemOccupancy(final String basketReference,
                                   final List<UpdateBasketItemSupplement> updateBasketItemRequest,
                                   final String ifMatch);

  List<Basket> getBasketsByReferences(List<String> bookingReferences);

  BasketStatusResponse preCheckInBasket(final String basketReference, Boolean isCiol);

  BasketStatusResponse preCheckOutBasket(final String basketReference);

  Basket changeStatus(final String bookingRef, final String status);

  Basket addPromotionToBasket(String basketReference,
      PromotionsInformationRequest promotionsInformationRequestDto, String requestModifyTimestamp);

  Basket changeIdContext(final String basketReference, final String idContext);

  Basket createBasketReservation(final CreateBasketRequest createBasketRequest);
}
