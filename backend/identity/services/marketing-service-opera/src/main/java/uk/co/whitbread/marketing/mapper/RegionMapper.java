package uk.co.whitbread.marketing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.bart.unified.api.SharedDataRequestResponse;
import uk.co.whitbread.marketing.model.Region;
import uk.co.whitbread.marketing.model.RegionsResponse;

@Mapper(componentModel = "spring")
public interface RegionMapper {

  @Mapping(target = "regions", source = "sharedDataRequestResult.regions.region")
  RegionsResponse toRegionsResponse(SharedDataRequestResponse sharedDataRequestResponse);

  @Mapping(target = "id", source = "regionID")
  @Mapping(target = "description", source = "regionDescription")
  @Mapping(target = "active", source = "active")
  Region toRegion(uk.co.whitbread.bart.unified.api.Region region);

}
