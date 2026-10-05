package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BookingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CcuiPaymentRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.InitiatePaymentResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentCcuiDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentCcuiRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentCcuiResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ProcessAmendRequestDto;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentRequest;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentsConfirmation;
import uk.co.whitbread.reservation.domain.model.payment.out.InitiatePaymentResponse;
import uk.co.whitbread.reservation.domain.model.payment.out.ccui.PaymentCcuiResponse;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

  InitiatePaymentResponse toModel(InitiatePaymentResponseDto initiatePaymentResponseDto);

  PaymentRequestDto toDto(PaymentRequest paymentRequest);

  ProcessAmendRequestDto toDto(PaymentsConfirmation paymentsConfirmation, String ccAgentId);

  @Mapping(target = "paymentRequest", expression = "java(toPaymentCcuiRequestDto(confirmAmendLogicRequest, hotelId))")
  CcuiPaymentRequestDto toCcuiDto(ConfirmAmendLogicRequest confirmAmendLogicRequest, String hotelId);

  @Mapping(target = "booking", expression = "java(toBookingDto(confirmAmendLogicRequest, hotelId))")
  @Mapping(target = "payment", expression = "java(toPaymentCcuiDto(confirmAmendLogicRequest))")
  PaymentCcuiRequestDto toPaymentCcuiRequestDto(ConfirmAmendLogicRequest confirmAmendLogicRequest, String hotelId);

  @Mapping(target = "channel", source = "confirmAmendLogicRequest.bookingChannel.channel")
  @Mapping(target = "language",
      expression = "java(confirmAmendLogicRequest.getBookingChannel().getLanguage().toLowerCase())")
  @Mapping(target = "journey", constant = "AMEND")
  @Mapping(target = "type", source = "confirmAmendLogicRequest.paymentOptionSelected")
  @Mapping(target = "businessSite.identifier", source = "hotelId")
  @Mapping(target = "businessSite.type", constant = "HOTEL")
  BookingDto toBookingDto(ConfirmAmendLogicRequest confirmAmendLogicRequest, String hotelId);

  @Mapping(target = "billing.email", source = "confirmAmendLogicRequest.emailAddress")
  @Mapping(target = "type", source = "confirmAmendLogicRequest.paymentRequest.payment.type")
  @Mapping(target = "subType", source = "confirmAmendLogicRequest.paymentRequest.payment.subType")
  PaymentCcuiDto toPaymentCcuiDto(ConfirmAmendLogicRequest confirmAmendLogicRequest);

  PaymentCcuiResponse toCcuiModel(PaymentCcuiResponseDto initiatePaymentResponseDto);

}
