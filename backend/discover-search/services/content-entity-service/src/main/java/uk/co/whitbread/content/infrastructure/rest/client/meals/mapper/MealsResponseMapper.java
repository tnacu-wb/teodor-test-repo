package uk.co.whitbread.content.infrastructure.rest.client.meals.mapper;

import java.util.Arrays;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;
import uk.co.whitbread.content.domain.model.meals.out.SoftBundle;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.in.MealsInfoResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.in.SoftBundleAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.out.MealsRequestAemDto;

@Mapper(componentModel = "spring")
public interface MealsResponseMapper {

  @Mapping(source = "rate", target = "rate", qualifiedByName = "splitByComma")
  @Mapping(source = "roomClass", target = "roomClass", qualifiedByName = "splitByComma")
  @Mapping(source = "isOptional", target = "optional")
  SoftBundle toSoftBundleModel(SoftBundleAemDto dto);

  MealsInfoResponse toModel(MealsInfoResponseAemDto mealsInfoResponseAemDto);

  MealsRequestAemDto toDto(MealsRequest mealsRequest);

  @Named("splitByComma")
  default List<String> toSplitByCommaModel(String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    return Arrays.stream(raw.split(","))
            .map(String::trim)
            .toList();
  }
}
