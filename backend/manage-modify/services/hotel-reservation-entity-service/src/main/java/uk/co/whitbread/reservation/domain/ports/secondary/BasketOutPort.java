package uk.co.whitbread.reservation.domain.ports.secondary;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.BasketStatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.PaymentOptionEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.basket.allowances.UpdateAllowancesRequest;
import uk.co.whitbread.reservation.domain.model.in.EmailRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.RefundResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentRequest;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentsConfirmation;
import uk.co.whitbread.reservation.domain.model.payment.in.RefundRequest;
import uk.co.whitbread.reservation.domain.model.payment.out.InitiatePaymentResponse;
import uk.co.whitbread.reservation.domain.model.payment.out.ccui.PaymentCcuiResponse;

public interface BasketOutPort {

  Optional<BasketResponse> getBasketByReference(final String reference);

  BasketResponse getBasketById(String basketReference);

  BasketResponse createBasket(final String hotelId, final String originalBasketId,
                              final BasketStatusEnum basketStatus,
                              final String migratedResNo, final PaymentOptionEnum paymentOption, String paymentId,
                              String channel, String subChannel);

  default BasketResponse createBasket(final String hotelId, final String channel, String subChannel) {
    return createBasket(hotelId, null, null, null, null, null, channel, subChannel);
  }

  BasketResponse createBasket(CreateBasketRequestDto createBasketRequest);

  BasketResponse addReservationsToBasket(String reference,
      String lastModifiedETag, OhipReservationResponse ohipReservationResponse,
      boolean isMigratedReservation, boolean isOccupancySupplementApplicable);

  BasketResponse addReservationsToBasket(String reference,
      String lastModifiedETag, OhipReservationResponse ohipReservationResponse,
      boolean isMigratedReservation, boolean isOccupancySupplementApplicable, Integer noOfAdults,
      boolean isOta);

  BasketResponse addReservationsToBasket(String reference,
      String lastModifiedETag, CopyReservationsResponse copyReservationsResponse,
      boolean isOccupancySupplementApplicable, Map<String, Boolean> hasOccupancySupMap);

  BasketResponse linkAmendReservationsInBasket(final String reference, final String channel,
      final String lastModifiedETag, Map<String, String> linkAmendReservations);

  void cancelBasket(String reference, boolean isFailed, boolean sendEmail,
      final List<Deposits> refundedDeposits);

  void setErroredBooking(String reference, boolean isErroredBooking, final BasketError basketError);

  RefundResponse triggerRefundRequest(final ReservationByBasketRefResponse reservations,
      final String basketReference);

  RefundResponse triggerRefundRequest(String basketReference, RefundRequest refundRequest);

  BasketResponse removeItem(String basketReference, String basketItem, String lastModifiedETag);

  BasketResponse removeItems(String basketReference, List<String> basketItem, String lastModifiedETag);

  void deleteBasket(String basketReference, String lastModifiedETag);

  void saveCharges(DepositFoliosResponse depositFolios);

  DepositFoliosResponse getCharges(String reservationId);

  DepositFoliosResponse getChargesByReservationIds(List<String> reservationIds);

  void triggerEmailConfirmation(EmailRequest emailRequest);

  InitiatePaymentResponse initiatePayment(String basketReference,
      PaymentRequest paymentRequest);

  void processAmend(String basketReference, PaymentsConfirmation paymentsConfirmation, @Nullable String ccAgentId);

  PaymentCcuiResponse initiateCcuiPaymentProcess(ConfirmAmendLogicRequest confirmAmendLogicRequest, String hotelId);

  String updateAllowances(String basketReference, UpdateAllowancesRequest updateAllowancesRequest,
                          final String lastModifiedETag);

  String updateOccupancySupplementFlag(final String basketRef,
                                       final BasketItemResponse basketReservationReference,
                                       final boolean newSupplementFlagValue,
                                       final String lastModifiedETag);

  String updateOccupancySupplementFlag(String basketRef,
      List<BasketItemResponse> items,
      String lastModifiedETag);

  BasketResponse addPromotionToBasket(String basketReference, String promotionCode,
      PromoKind promoKind, String lastModifiedETag);

  void changeIdContext(String reference, String idContex);

  BasketDto createBasketReservation(CreateBasketRequestReservationDto createBasketRequestReservationDto);

  Pair<List<BasketItemDto>, List<AddBasketItemTypeDto>>
      addReservationsBasketItemsAndTypes(ReservationsDetailsEnhancedResponse operaReserv);
}
