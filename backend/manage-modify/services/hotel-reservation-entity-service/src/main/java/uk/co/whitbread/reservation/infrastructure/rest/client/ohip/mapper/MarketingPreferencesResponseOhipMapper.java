package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MarketingPreferencesResponseDto;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;


@Mapper(componentModel = "spring")
public interface MarketingPreferencesResponseOhipMapper {

  MarketingPreferencesResponse toModel(
      MarketingPreferencesResponseDto marketingPreferencesResponseDto);

}
