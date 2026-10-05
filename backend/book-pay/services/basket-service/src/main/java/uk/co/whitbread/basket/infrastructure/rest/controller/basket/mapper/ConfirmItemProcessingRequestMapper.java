package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.ConfirmItemProcessingRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ConfirmItemProcessingRequestDto;

@Mapper(componentModel = "spring")
public interface ConfirmItemProcessingRequestMapper {
  ConfirmItemProcessingRequest toDomainModel(
      ConfirmItemProcessingRequestDto confirmItemProcessingRequestDto);
}
