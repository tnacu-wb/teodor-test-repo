package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper;

import java.util.Arrays;
import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in.CdhReservationSearchCriteriaDto;

@Mapper(componentModel = "spring", imports = {Arrays.class})
public interface CdhReservationSearchCriteriaDtoMapper {

  ReservationSearchCriteria toModel(CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto);
}
