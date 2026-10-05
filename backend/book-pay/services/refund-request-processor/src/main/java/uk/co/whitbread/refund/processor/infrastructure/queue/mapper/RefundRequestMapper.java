package uk.co.whitbread.refund.processor.infrastructure.queue.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.refund.processor.domain.model.in.RefundRequest;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.RefundRequestEvent;

@Mapper(componentModel = "spring")
public interface  RefundRequestMapper {

  RefundRequest toDomain(RefundRequestEvent refundRequestEvent);

}
