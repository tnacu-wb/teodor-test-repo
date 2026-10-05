package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.MarketingPreferencesResponseDto;

@Mapper(componentModel = "spring")
public interface MarketingPreferencesResponseMapper {

  MarketingPreferencesResponseDto toDto(MarketingPreferencesResponse depositsResponse);

}
