package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundResponseDto;
import uk.co.whitbread.reservation.domain.model.out.RefundResponse;
import uk.co.whitbread.reservation.domain.model.payment.in.RefundRequest;

@Mapper(componentModel = "spring")
public interface RefundMapper {

  RefundRequestDto toRequestDto(RefundRequest refundRequestDto);

  RefundResponse toResponseModel(RefundResponseDto responseDto);

}
