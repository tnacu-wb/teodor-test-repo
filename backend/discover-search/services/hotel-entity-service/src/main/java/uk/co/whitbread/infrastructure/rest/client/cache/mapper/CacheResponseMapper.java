package uk.co.whitbread.infrastructure.rest.client.cache.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsOpeningSoonResult;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsWithFacilityFilterResult;
import uk.co.whitbread.domain.model.srp.out.HotelsOpeningSoonModel;
import uk.co.whitbread.domain.model.srp.out.HotelsWithFilterModel;

@Mapper(componentModel = "spring")
public interface CacheResponseMapper {

  HotelsWithFilterModel toHotelListModel(HotelsWithFacilityFilterResult response);

  HotelsOpeningSoonModel toHotelsOpeningSoonModel(HotelsOpeningSoonResult response);
}
