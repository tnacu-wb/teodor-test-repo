package uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.MANAGER_APPROVAL_PREFIX;

import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ItemType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ListOfValues;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReason;
import uk.co.whitbread.ohip.domain.model.lov.out.CancellationReasonsResponse;

@Mapper(componentModel = "spring")
public interface CancellationReasonsOhipMapper {

  default CancellationReasonsResponse toCancellationReasonsResponseModel(
      ListOfValues listOfValues) {

    List<CancellationReason> cancellationReasons = listOfValues.getListOfValues().getItems()
        .stream()
        .map(this::createCancellationReason)
        .sorted(
            Comparator.comparing(
                reason -> !reason.getDescription().startsWith(MANAGER_APPROVAL_PREFIX)))
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
        .managerApprovalNeeded(requiresManagerApproval(itemType))
        .build();
  }

  private boolean requiresManagerApproval(ItemType itemType) {
    return itemType.getName().startsWith(MANAGER_APPROVAL_PREFIX) || itemType.getDescription()
        .startsWith(MANAGER_APPROVAL_PREFIX);
  }

}
