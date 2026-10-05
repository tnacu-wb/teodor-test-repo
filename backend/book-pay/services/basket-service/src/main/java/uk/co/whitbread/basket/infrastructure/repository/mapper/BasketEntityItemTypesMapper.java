package uk.co.whitbread.basket.infrastructure.repository.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketItemTypeEntity;

@Mapper(componentModel = "spring")
public class BasketEntityItemTypesMapper {

  @Named("toDomainModelItemTypes")
  public Map<String, Set<String>> toModel(List<BasketItemTypeEntity> itemTypes) {
    final var domainModel = new HashMap<String, Set<String>>();
    itemTypes.forEach(itemTypeEntity -> domainModel.put(itemTypeEntity.getType(),
        Set.copyOf(itemTypeEntity.getConfirmationData())));
    return Collections.unmodifiableMap(domainModel);
  }

  @Named("toEntityModelItemTypes")
  public List<BasketItemTypeEntity> toEntityModel(Map<String, Set<String>> itemTypes) {
    final var entityModel = new ArrayList<BasketItemTypeEntity>();
    itemTypes.entrySet().forEach(itemTypeEntity -> entityModel.add(BasketItemTypeEntity.builder()
        .type(itemTypeEntity.getKey())
        .confirmationData(List.copyOf(itemTypeEntity.getValue()))
        .build()));
    return Collections.unmodifiableList(entityModel);
  }
}
