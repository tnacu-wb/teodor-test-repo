package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.payapp.domain.model.in.InitializeApplicationRequest;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppInitRequestDto;

@Mapper(componentModel = "spring")
public interface AppInitRequestMapper {

  WorldlineAppInitRequestDto toModel(InitializeApplicationRequest initializeApplicationRequest);

}
