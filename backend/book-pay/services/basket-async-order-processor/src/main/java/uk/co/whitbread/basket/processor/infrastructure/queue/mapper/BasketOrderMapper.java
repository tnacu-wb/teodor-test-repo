package uk.co.whitbread.basket.processor.infrastructure.queue.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.basket.processor.domain.model.in.BasketOrder;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketOrderEvent;

@Mapper(componentModel = "spring")
public interface BasketOrderMapper {

  BasketOrder toDomain(BasketOrderEvent basketOrderEvent);
}
