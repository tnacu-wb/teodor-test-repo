package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payapp.domain.model.out.SubmitApplicationResponse;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.SubmitApplicationDto;

@Mapper(componentModel = "spring")
public interface SubmitApplicationResponseMapper {

  @Mapping(target = "message", source = "data")
  SubmitApplicationResponse toModel(SubmitApplicationDto submitApplicationResponseDto);
}
