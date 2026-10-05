package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.out.DashboardElementDto;

@Mapper(componentModel = "spring")
public interface DashboardElementMapper {

  DashboardElementDto toDto(DashboardElement dashboardElement);
}