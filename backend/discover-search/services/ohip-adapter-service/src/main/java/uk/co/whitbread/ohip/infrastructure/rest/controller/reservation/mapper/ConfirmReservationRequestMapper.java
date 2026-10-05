package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto;

@Mapper(componentModel = "spring", uses = {
    PaymentCardMapper.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ConfirmReservationRequestMapper {

  ConfirmReservationRequest toConfirmReservationRequestModel(ConfirmReservationRequestDto confirmReservationRequestDto);


}
