package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioChargeDto;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;

@Mapper(componentModel = "spring")
public interface CancelReservationRequestOhipMapper {

  @Mapping(expression = "java(toChargesByReservationIdsDto(prepaidDeposits))", target = "chargesByReservationIds")
  CancelReservationRequestDto toDto(
      CancelReservationRequest cancelReservationRequest,
      List<DepositFoliosResponse> prepaidDeposits);

  List<DepositFolioChargeDto> toDepositFolioChargeListDto(List<DepositFolioCharge> value);

  DepositFolioChargeDto toDepositFolioChargeDto(DepositFolioCharge value);


  default Map<String, List<DepositFolioChargeDto>> toChargesByReservationIdsDto(
      List<DepositFoliosResponse> prepaidDeposits) {
    Map<String, List<DepositFolioChargeDto>> chargesByReservationIds = new HashMap<>();
    if (Objects.isNull(prepaidDeposits)) {
      return chargesByReservationIds;
    }
    prepaidDeposits.forEach(prepaidDeposit -> {
      List<DepositFolioChargeDto> charges = new ArrayList<>();
      prepaidDeposit.getDepositFolios().forEach(depositFolio -> {
        charges.addAll(toDepositFolioChargeListDto(depositFolio.getCharges()));
        chargesByReservationIds.put(depositFolio.getReservationId(), charges);
      });
    });
    return chargesByReservationIds;
  }
}
