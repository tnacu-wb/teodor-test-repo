package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.BillingResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.BillingResponseDto;

@Mapper(componentModel = "spring", uses = {AddressMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BillingMapper {

  @Mapping(target = "email", source = "email", defaultValue = "")
  @Mapping(target = "firstName", source = "firstName", defaultValue = "")
  @Mapping(target = "lastName", source = "lastName", defaultValue = "")
  @Mapping(target = "telephone", source = "telephone", defaultValue = "")
  @Mapping(target = "title", source = "title", defaultValue = "")
  BillingResponseDto toDto(BillingResponse response);

}
