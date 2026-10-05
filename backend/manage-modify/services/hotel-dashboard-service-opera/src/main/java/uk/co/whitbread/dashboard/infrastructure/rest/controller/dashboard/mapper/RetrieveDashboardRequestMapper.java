package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.in.RetrieveDashboardRequestDto;

@Mapper(componentModel = "spring")
public interface RetrieveDashboardRequestMapper {

  RetrieveDashboardRequest toModel(RetrieveDashboardRequestDto retrieveDashboardRequestDto);
}