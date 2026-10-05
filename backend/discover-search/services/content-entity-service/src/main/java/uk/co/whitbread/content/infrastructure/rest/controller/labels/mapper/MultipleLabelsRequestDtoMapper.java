package uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.labels.in.MultipleLabelsRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.MultipleLabelsRequestDto;

@Mapper(componentModel = "spring", imports = CategoryEnumDto.class)
public interface MultipleLabelsRequestDtoMapper {

  @Mapping(source = "categories", target = "categories")
  MultipleLabelsRequest toDomainModel(MultipleLabelsRequestDto multipleLabelsRequestDto);
}
