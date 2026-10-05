package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.mapper;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface PriceFinderConfigQueryParamsMapper {

  String DEFAULT_BRAND = "pi";
  String DEFAULT_COUNTRY = "gb";
  String DEFAULT_LANGUAGE = "en";
  String DEFAULT_PATH = "path/to/content";

  @Named("toBrandModel")
  default String toBrandModel(String brand) {
    return isBlank(brand) ? DEFAULT_BRAND : brand.toLowerCase();
  }

  @Named("toCountryModel")
  default String toCountryModel(String country) {
    return isBlank(country) ? DEFAULT_COUNTRY : country.toLowerCase();
  }

  @Named("toLanguageModel")
  default String toLanguageModel(String language) {
    return isBlank(language) ? DEFAULT_LANGUAGE : language.toLowerCase();
  }

  @Named("toPathModel")
  default String toPathModel(String path) {
    return isBlank(path) ? DEFAULT_PATH : path.toLowerCase();
  }
}
