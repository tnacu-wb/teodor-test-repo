package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Arrays;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.DepositFolioRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class DepositFoliosRequestMapper {

  @Mapping(target = "paymentId", expression = "java(injectPaymentId(confirmReservationRequest))")
  public abstract DepositFolioRequest toDepositFolioRequestModel(ConfirmReservationRequest confirmReservationRequest);

  public DepositFolioRequest toDepositFolioRequestModel(CancelReservationRequest cancelReservationRequest,
      String reservationId) {

    return DepositFolioRequest.builder().hotelId(cancelReservationRequest.getHotelId())
        .reservationId(reservationId).paymentOption(PaymentOption.PAY_NOW)
        .paymentMethod(cancelReservationRequest.getDefaultPaymentMethod())
        .isCancelRequest(true).build();
  }

  protected String injectPaymentId(ConfirmReservationRequest confirmReservationRequest) {
    return (confirmReservationRequest.getThreeDSIndicator() == null)
            ? confirmReservationRequest.getPaymentId()
            : confirmReservationRequest.getPaymentId() + "|" + confirmReservationRequest.getThreeDSIndicator();
  }
}