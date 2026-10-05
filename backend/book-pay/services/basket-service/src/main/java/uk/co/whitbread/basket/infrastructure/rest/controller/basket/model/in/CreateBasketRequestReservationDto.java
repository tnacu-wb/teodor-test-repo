package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;


@Data
@Builder
@NoArgsConstructor
public class CreateBasketRequestReservationDto
    implements SelfValidation<CreateBasketRequestReservationDto> {

  private String hotelId;
  private UserInfoDto userInfoDto;
  private ReservationBasketInfoDto reservationBasketInfoDto;
  private BasketStatus basketStatus;
  private PaymentInfoDto paymentInfoDto;
  private ChannelInfoDto channelInfoDto;
  private BasketItemInfoDto basketItemInfoDto;

  public CreateBasketRequestReservationDto(
      String hotelId,
      UserInfoDto userInfoDto,
      ReservationBasketInfoDto reservationBasketInfoDto,
      BasketStatus basketStatus,
      PaymentInfoDto paymentInfoDto,
      ChannelInfoDto channelInfoDto,
      BasketItemInfoDto basketItemInfoDto) {

    this.hotelId = hotelId;
    this.userInfoDto = userInfoDto;
    this.reservationBasketInfoDto = reservationBasketInfoDto;
    this.basketStatus = basketStatus;
    this.paymentInfoDto = paymentInfoDto;
    this.channelInfoDto = channelInfoDto;
    this.basketItemInfoDto = basketItemInfoDto;
    this.validateSelf();
  }
}

