package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.infrastructure.rest.client.availability.model.ItemInventoryRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.packages.model.PackagesRequestOhipDto;

@Mapper(componentModel = "spring")
public interface PackagesRequestMapper {

  PackagesRequestOhipDto toOhipDto(PackagesRequest packagesRequest);

  ItemInventoryRequestOhipDto toItemInventoryRequestOhipDto(PackagesRequest packagesRequest, List<String> itemCodes);
}
