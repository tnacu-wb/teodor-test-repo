package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChargeCriteriaType;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class DepositFolioChargeMapper {

  @Mapping(source = "chargeCriteriaType.postingReference", target = "reference")
  @Mapping(source = "chargeCriteriaType.postingQuantity", target = "quantity")
  @Mapping(source = "chargeCriteriaType.price", target = "currencyAmount")
  public abstract DepositFolioCharge toDepositFolioChargeModel(ChargeCriteriaType chargeCriteriaType);

}
