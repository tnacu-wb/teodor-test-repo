package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.in.DiscountRequest;
import uk.co.whitbread.basket.domain.model.payments.out.DiscountResponse;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.UpdateDiscountRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.out.UpdateDiscountResponseDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CcuiUpdateDiscountMapper {

  DiscountRequest toModel(UpdateDiscountRequestDto discountRequestDto);

  UpdateDiscountResponseDto toDto(DiscountResponse discountResponse);
}
