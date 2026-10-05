package uk.co.whitbread.infrastructure.rest.client.availabilitycache.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availabilitycache.in.AvailabilityCacheSearchCriteria;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.in.AvailabilityCacheRequest;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AvailabilityCacheSearchMapper {

  AvailabilityCacheRequest toDto(AvailabilityCacheSearchCriteria availabilityCacheSearchCriteria);

}
