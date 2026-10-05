package uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.labels.in.LabelsRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.LabelsRequestDto;


@Mapper(componentModel = "spring")
public interface LabelsRequestDtoMapper {

  LabelsRequest toDomainModel(LabelsRequestDto labelsRequestDto);
}
