package uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.labels.out.ExtrasLabel;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.out.ExtrasLabelDto;


@Mapper(componentModel = "spring")
public interface ExtrasDtoMapper {

  ExtrasLabelDto toDto(ExtrasLabel extrasLabels);
}
