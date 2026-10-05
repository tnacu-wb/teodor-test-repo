package uk.co.whitbread.refund.processor.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.in.TokenRefundRequestDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TokenRefundDtoMapper {

  TokenRefund toDomain(TokenRefundRequestDto tokenRefundRequestDto);
}
