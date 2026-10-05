package uk.co.whitbread.refund.processor.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.out.RefundResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RefundResponseDtoMapper {

  RefundResponseDto toDto(RefundResponse refundResponse);
}
