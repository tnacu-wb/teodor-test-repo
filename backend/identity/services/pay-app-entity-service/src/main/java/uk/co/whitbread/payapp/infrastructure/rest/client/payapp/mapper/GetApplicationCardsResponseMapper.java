package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payapp.domain.model.out.GetAppCardsResponse;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetAppCardsResponseDto;

@Mapper(componentModel = "spring")
public interface GetApplicationCardsResponseMapper {

  @Mapping(target = "appCards", source = "data")
  GetAppCardsResponse toModel(GetAppCardsResponseDto getAppCardsResponseDto);

}
