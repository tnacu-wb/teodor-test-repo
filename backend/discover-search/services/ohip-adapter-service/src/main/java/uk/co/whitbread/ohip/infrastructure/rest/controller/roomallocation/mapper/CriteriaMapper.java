package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.Criteria;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in.CriteriaDto;

@Mapper(componentModel = "spring")
public interface CriteriaMapper {

  Criteria toModel(CriteriaDto criteriaDto);

}
