package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.spending.domain.model.out.UpcomingSpendingResponse;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.UpcomingSpendingResponseDto;

@Mapper(componentModel = "spring")
public interface UpcomingSpendingResponseDtoMapper {

  UpcomingSpendingResponseDto toDto(UpcomingSpendingResponse upcomingSpendingResponse);
}
