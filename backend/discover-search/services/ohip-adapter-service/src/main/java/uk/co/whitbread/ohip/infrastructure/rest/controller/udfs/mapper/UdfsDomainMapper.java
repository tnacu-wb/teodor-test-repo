package uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.udfs.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.model.in.UdfsRequestDto;

@Mapper(componentModel = "spring")
public interface UdfsDomainMapper {

  UpdateReservationUdfsRequest toDomainModel(UdfsRequestDto dto);
}
