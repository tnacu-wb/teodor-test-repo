package uk.co.whitbread.basket.domain.ports.secondary;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationInfoPaymentType;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.UdfsRequestDto;

public interface BasketOutPort {

  Basket createBasket(final CreateBasketRequest createBasketRequest);

  Basket updateBasket(final Basket basket);

  Optional<Basket> getBasketByReference(final String reference);

  Basket getBasketById(String basketId);

  void updateBasketPayment(final Basket basket);

  void updateBasketStatus(final String reference, BasketStatus basketStatus, final Optional<Instant> cleanUpBaseTime);

  void updatePollingStartedAt(final String reference, final String timestamp);

  void deleteBasket(final String threeLetterHotelId, final String sortKey);

  List<Basket> getBasketsByReferences(List<String> references);

  void updateCharacterUdfs(final UdfsRequestDto characterUDFsDto);

  Basket createBasketReservation(final CreateBasketRequest createBasketRequest);

  List<ReservationInfoPaymentType> getPaymentType(String hotelId, Set<String> sourceIds);

  void postReservationPreregister(PreCheckInRequestDto preCheckInRequestDto);

  ReservationByBasketRefResponseDto getReservationDetails(String hotelId, String reservationIds);

  void updateBasketWithPaymentStatus(final Basket basket);
}
