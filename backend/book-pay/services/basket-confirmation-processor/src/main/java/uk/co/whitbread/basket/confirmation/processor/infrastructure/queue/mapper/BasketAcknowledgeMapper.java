package uk.co.whitbread.basket.confirmation.processor.infrastructure.queue.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.queue.model.in.BasketAcknowledgeEventDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BasketAcknowledgeMapper {

  BasketAcknowledge toModel(BasketAcknowledgeEventDto basketAcknowledgeEventDto);
}
