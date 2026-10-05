package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.hotel.content.generated.models.UpsellItemsDto;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.ExtrasDto;

@Mapper(componentModel = "spring")
public interface ExtrasMapper {

  @Mapping(target = "name", source = "extrasLabel.name")
  @Mapping(target = "id", source = "meal.id")
  ExtrasDto toDto(Meal meal, Integer available, uk.co.whitbread.hotel.content.generated.models.ExtrasDto extrasLabel);

  @Mapping(target = "name", source = "upsellItemsDto.name")
  @Mapping(target = "id", source = "meal.id")
  @Mapping(target = "imageSrc", source = "upsellItemsDto", qualifiedByName = "toImgSrc")
  ExtrasDto toDtoFromUpsell(Meal meal, Integer available, UpsellItemsDto upsellItemsDto);

  @Named("toImgSrc")
  default String toImgSrc(UpsellItemsDto upsellItemsDto) {
    return upsellItemsDto.getImages().get(0);
  }
}
