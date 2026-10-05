package uk.co.whitbread.infrastructure.rest.client.migrationstatus.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.in.MigrationStatusRequest;

@Mapper(componentModel = "spring")
public interface MigrationStatusRequestMapper {

  MigrationStatusRequest toDto(HotelsMigrationStatusRequest hotelsMigrationStatusRequest);
}
