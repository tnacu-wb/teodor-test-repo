package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositDto;
import uk.co.whitbread.reservation.domain.model.out.DepositFolio;

@Mapper(componentModel = "spring", uses = {DepositFolioResponseChargeMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepositFolioResponseMapper {

  DepositFolio toModel(PrepaidDepositDto depositFolio);
}
