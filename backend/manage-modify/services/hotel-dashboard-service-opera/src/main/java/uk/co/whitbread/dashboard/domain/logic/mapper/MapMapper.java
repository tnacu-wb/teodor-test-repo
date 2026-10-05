package uk.co.whitbread.dashboard.domain.logic.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoMap;
import uk.co.whitbread.dashboard.domain.model.out.Map;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MapMapper {

  @Mapping(target = "latitude", source = "latitude")
  @Mapping(target = "longitude", source = "longitude")
  Map infoMapToMap(InfoMap map);

}
