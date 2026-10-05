package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Arrays;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteria;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolio;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {DepositFolioChargeMapper.class},
    imports = {Arrays.class})
public abstract class DepositFolioCriteriaMapper {

  @Mapping(source = "depositFolioCriteria.criteria.reservationId.id", target = "reservationId")
  @Mapping(source = "depositFolioCriteria.criteria.hotelId", target = "hotelId")
  @Mapping(source = "depositFolioCriteria.criteria.charges", target = "charges")
  public abstract DepositFolio toDepositFolioModel(DepositFolioCriteria depositFolioCriteria);

}
