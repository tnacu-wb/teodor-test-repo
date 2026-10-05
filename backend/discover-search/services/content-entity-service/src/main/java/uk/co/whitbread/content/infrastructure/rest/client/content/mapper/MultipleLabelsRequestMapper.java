package uk.co.whitbread.content.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.labels.in.MultipleLabelsRequest;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.MultipleLabelsRequestDto;

@Mapper(componentModel = "spring")
public interface MultipleLabelsRequestMapper {

  MultipleLabelsRequest toDomainModel(MultipleLabelsRequestDto labelsRequestDto);

  MultipleLabelsRequestDto toDtoModel(MultipleLabelsRequest labelsRequest);
}
