package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public class BasketItemTypesMapper {

  @Named("toDtoItemTypes")
  Set<String> toDtoItemTypes(Map<String, Set<String>> itemTypes) {
    return Collections.unmodifiableSet(itemTypes.keySet());
  }
}
