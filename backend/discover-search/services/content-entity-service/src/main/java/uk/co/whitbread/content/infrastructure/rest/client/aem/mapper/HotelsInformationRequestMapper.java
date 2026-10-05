package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out.HotelsInformationDto;

@Mapper(componentModel = "spring")
public interface HotelsInformationRequestMapper {

  HotelsInformationDto toDtoModel(HotelsInformationRequest hotelsInformationRequest);
}
