package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChargeDto;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;

@Mapper(componentModel = "spring")
public interface DepositFolioChargeMapper {

  @Mapping(target = "chargeAmount", source = "currencyAmount")
  @Mapping(target = "postingReference", source = "reference")
  @Mapping(target = "postingQuantity", source = "quantity", defaultValue = "1")
  ChargeDto toDto(DepositFolioCharge depositFolioCharge);

}
