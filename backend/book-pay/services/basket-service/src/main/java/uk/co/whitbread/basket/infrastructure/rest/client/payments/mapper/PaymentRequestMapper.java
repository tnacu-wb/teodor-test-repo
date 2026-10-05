package uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.ChannelEnum;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.JourneyEnum;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.LanguageEnum;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.TypeEnum;
import uk.co.whitbread.basket.generated.models.payments.PaymentRequestDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundRequestDto;

@Mapper(componentModel = "spring", uses = {RoomTypeRequestMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PaymentRequestMapper {

  @Named("toTypeDto")
  static TypeEnum toTypeDto(String value) {
    return TypeEnum.fromValue(value);
  }

  @Named("toLanguageDto")
  static LanguageEnum toLanguageDto(String value) {
    return LanguageEnum.fromValue(value);
  }

  @Named("toJourneyDto")
  static JourneyEnum toJourneyDto(String value) {
    return JourneyEnum.fromValue(value);
  }

  @Named("toChannelDto")
  static ChannelEnum toChannelDto(String value) {
    return ChannelEnum.fromValue(value);
  }

  @Mapping(source = "booking.type", target = "booking.type", qualifiedByName = "toTypeDto")
  @Mapping(source = "booking.language", target = "booking.language", qualifiedByName = "toLanguageDto")
  @Mapping(source = "booking.journey", target = "booking.journey", qualifiedByName = "toJourneyDto")
  @Mapping(source = "booking.channel", target = "booking.channel", qualifiedByName = "toChannelDto")
  PaymentRequestDto toPaymentRequestDto(PaymentRequest createPayment);

  TokenRefundRequestDto toTokenRefundRequestDto(RefundRequest refundRequest);
}
