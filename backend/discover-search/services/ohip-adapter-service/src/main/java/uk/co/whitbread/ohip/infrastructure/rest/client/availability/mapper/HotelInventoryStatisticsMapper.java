package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.inventory.NumericCategorySummaryType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.inventory.StatisticSetType;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsDateItem;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsInventoryItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HotelInventoryStatisticsMapper {

  @Mapping(target = "inventoryItemList",
      source = "statistics", qualifiedByName = "resultMapper")
  StatisticsDateItem toStatisticsDateModel(StatisticSetType statistics);

  @Named("resultMapper")
  default List<StatisticsInventoryItem> toResultForModel(StatisticSetType statistics) {
    return statistics.getInventory()
        .stream()
        .map(this::mapStatisticsIventoryItemModel)
        .toList();
  }

  private StatisticsInventoryItem mapStatisticsIventoryItemModel(NumericCategorySummaryType categorySummaryType) {
    return StatisticsInventoryItem.builder().code(categorySummaryType.getCode()).value(categorySummaryType.getValue())
        .build();
  }


}
