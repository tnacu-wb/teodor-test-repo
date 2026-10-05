package uk.co.whitbread.infrastructure.rest.client.migrationstatus.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.out.MigrationStatus;

@Mapper(componentModel = "spring")
public interface MigrationStatusResponseMapper {

  HotelsMigrationStatusResponse toModel(MigrationStatus migrationStatus);
}
