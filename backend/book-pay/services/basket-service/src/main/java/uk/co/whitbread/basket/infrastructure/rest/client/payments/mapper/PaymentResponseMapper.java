package uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.ChannelEnum;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.JourneyEnum;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.LanguageEnum;
import uk.co.whitbread.basket.generated.models.payments.BookingDto.TypeEnum;
import uk.co.whitbread.basket.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundResponseDto;

@Mapper(componentModel = "spring", uses = {RoomTypeResponseMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentResponseMapper {

  @Named("toTypeModel")
  static String toTypeModel(TypeEnum value) {
    return value.getValue();
  }

  @Named("toLanguageModel")
  static String toLanguageModel(LanguageEnum value) {
    return value != null ? value.getValue() : null;
  }

  @Named("toJourneyModel")
  static String toJourneyModel(JourneyEnum value) {
    return value.getValue();
  }

  @Named("toChannelModel")
  static String toChannelModel(ChannelEnum value) {
    return value.getValue();
  }

  @Mapping(source = "booking.type", target = "booking.type", qualifiedByName = "toTypeModel")
  @Mapping(source = "booking.language", target = "booking.language", qualifiedByName = "toLanguageModel")
  @Mapping(source = "booking.journey", target = "booking.journey", qualifiedByName = "toJourneyModel")
  @Mapping(source = "booking.channel", target = "booking.channel", qualifiedByName = "toChannelModel")
  PaymentResponse toModel(PaymentResponseDto clientResponse);

  RefundResponse toRefundModel(TokenRefundResponseDto refundResponseDto);
}
