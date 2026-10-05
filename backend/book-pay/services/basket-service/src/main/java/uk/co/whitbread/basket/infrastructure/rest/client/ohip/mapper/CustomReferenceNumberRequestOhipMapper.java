package uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.ohip.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.basket.generated.models.ohip.UpdateCustomReferenceNumberRequestDto;

@Mapper(componentModel = "spring")
public interface CustomReferenceNumberRequestOhipMapper {

  UpdateCustomReferenceNumberRequestDto toDto(UpdateCustomReferenceNumberRequest model);
}
