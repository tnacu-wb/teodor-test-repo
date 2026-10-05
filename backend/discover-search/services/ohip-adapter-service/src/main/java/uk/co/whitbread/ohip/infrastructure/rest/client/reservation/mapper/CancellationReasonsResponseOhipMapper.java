/*
package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ItemType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ListOfValues;

@Mapper(componentModel = "spring")
public interface CancellationReasonsResponseOhipMapper {

  default CancellationReasonsResponse toCancellationReasonsResponseModel(
      ListOfValues listOfValues) {

    List<CancellationReason> cancellationReasons = listOfValues.getListOfValues().getItems()
        .stream()
        .map(this::createCancellationReason)
        .toList();
    return CancellationReasonsResponse.builder().cancellationReasons(cancellationReasons)
        .build();
  }

  private CancellationReason createCancellationReason(ItemType itemType) {
    return CancellationReason.builder()
        .code(itemType.getCode())
        .description(itemType.getDescription())
        .name(itemType.getName())
        .active(itemType.getActive())
        .build();
  }

}
*/
