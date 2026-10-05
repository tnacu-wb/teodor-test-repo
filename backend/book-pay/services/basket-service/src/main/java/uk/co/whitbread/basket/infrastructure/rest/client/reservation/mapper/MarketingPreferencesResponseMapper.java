package uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.basket.generated.models.reservation.MarketingPreferencesResponseDto;

@Mapper(componentModel = "spring")
public interface MarketingPreferencesResponseMapper {

  MarketingPreferencesResponse toModel(MarketingPreferencesResponseDto marketingPreferencesResponseDto);

}
