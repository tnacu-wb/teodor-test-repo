package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentCard;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PaymentCardDto;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

  PaymentCard toModel(PaymentCardDto paymentCardDto);

}
