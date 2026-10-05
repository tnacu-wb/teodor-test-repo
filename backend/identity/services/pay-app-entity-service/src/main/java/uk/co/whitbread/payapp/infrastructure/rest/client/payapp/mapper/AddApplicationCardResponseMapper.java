package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payapp.domain.model.out.AddApplicationCardResponse;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAddApplicationCardResponseDto;

@Mapper(componentModel = "spring")
public interface AddApplicationCardResponseMapper {

  @Mapping(target = "cardGuid", source = "data.cardGuid")
  AddApplicationCardResponse toModel(
      WLAddApplicationCardResponseDto wlAddApplicationCardResponseDto);

}
