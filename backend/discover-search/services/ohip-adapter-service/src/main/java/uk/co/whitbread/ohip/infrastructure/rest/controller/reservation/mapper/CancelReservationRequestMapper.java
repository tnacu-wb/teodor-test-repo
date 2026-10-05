package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFolioChargeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CancelReservationRequestDto;

@Mapper(componentModel = "spring")
public interface CancelReservationRequestMapper {

  CancelReservationRequest toModel(CancelReservationRequestDto cancelReservationRequestDto);

  Map<String, List<DepositFolioCharge>> toDepositFolioChargeMapModel(Map<String, List<DepositFolioChargeDto>> value);

  List<DepositFolioCharge> toDepositFolioChargeListModel(List<DepositFolioChargeDto> value);

}
