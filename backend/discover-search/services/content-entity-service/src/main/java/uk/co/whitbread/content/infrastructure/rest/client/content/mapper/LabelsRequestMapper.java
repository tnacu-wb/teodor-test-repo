package uk.co.whitbread.content.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.labels.in.LabelsRequest;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LabelsRequestDto;

@Mapper(componentModel = "spring")
public interface LabelsRequestMapper {

  LabelsRequest toDomainModel(LabelsRequestDto labelsRequestDto);

  LabelsRequestDto toDtoModel(LabelsRequest labelsRequest);

  LabelsRequestDto toDtoModel(String country, String language, CategoryEnumDto category);
}
