package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.mapper;

import java.time.LocalDate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in.ConfirmItemProcessingRequestDto;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in.ConfirmItemProcessorDescriptionEnumDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true))
public interface ConfirmItemProcessingRequestBasketMapper {

  ConfirmItemProcessingRequestDto toDto(BasketAcknowledge basketAcknowledge);

  @AfterMapping
  default void toConfirmItemProcessingDto(
      @MappingTarget ConfirmItemProcessingRequestDto confirmItemProcessingRequestDto,
      BasketAcknowledge basketAcknowledge) {
    confirmItemProcessingRequestDto.setReqAction(basketAcknowledge.getData().get("reqAction"));
    if (basketAcknowledge.getStatus() == 1) {
      confirmItemProcessingRequestDto.setDescription(
          ConfirmItemProcessorDescriptionEnumDto.FAILED_TYPE.getValue());
    } else if (basketAcknowledge.getStatus() == 0) {
      confirmItemProcessingRequestDto.setDescription(
          ConfirmItemProcessorDescriptionEnumDto.COMPLETED_TYPE.getValue());
    }
    confirmItemProcessingRequestDto.setReportedAt(LocalDate.now().toString());
  }

}
