package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.out.UpsellItemsExtras;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.UpsellItemsExtrasDto;

@Mapper(componentModel = "spring")
public interface UpsellItemsExtrasMapper {

  List<UpsellItemsExtras> toDomainModel(List<UpsellItemsExtrasDto> upsellItemsExtrasDto);

  UpsellItemsExtras toDomainModel(GlobalConfigDto globalConfigDto);

}
