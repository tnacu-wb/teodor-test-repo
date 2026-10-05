package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChargeDto;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;

@Mapper(componentModel = "spring")
public interface DepositFolioResponseChargeMapper {

  @Mapping(target = "currencyAmount", source = "chargeAmount")
  @Mapping(target = "reference", source = "postingReference")
  @Mapping(target = "quantity", source = "postingQuantity")
  DepositFolioCharge toModel(ChargeDto chargeDto);
}
