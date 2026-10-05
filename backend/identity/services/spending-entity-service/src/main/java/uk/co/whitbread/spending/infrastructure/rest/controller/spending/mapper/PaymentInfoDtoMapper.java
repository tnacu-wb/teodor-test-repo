package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentData;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentValue;
import uk.co.whitbread.spending.domain.utils.AppConstants;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.PaymentInfoQueryParamsDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.PaymentDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.PaymentInfoResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.PaymentValueDto;

@Mapper(componentModel = "spring")
public interface PaymentInfoDtoMapper {

  PaymentInfoModel toModel(PaymentInfoQueryParamsDto paymentInfoQueryParamsDto,
      String authorization, String ipAddress);

  @Mapping(source = "data", target = "payments")
  PaymentInfoResponseDto toPaymentInfoResponseDto(PaymentInfoResponse paymentInfoResponse);

  @Mapping(source = "failureReason", target = "paymentFailed", qualifiedByName = "toPaymentFailedDto")
  @Mapping(source = "paymentValue", target = "paymentValue")
  PaymentDto toPaymentDto(PaymentData paymentData);

  @Mapping(source = "currencyCode", target = "currencySymbol", qualifiedByName = "toCurrencySymbolDto")
  PaymentValueDto toPaymentValueDto(PaymentValue paymentValue);

  @Named("toPaymentFailedDto")
  default boolean toPaymentFailedDto(String failureReason) {
    return !"N/A".equals(failureReason);
  }

  @Named("toCurrencySymbolDto")
  default String toCurrencySymbolDto(String currencyCode) {
    if (AppConstants.CURRENCY_CODE_GBP.equals(currencyCode)) {
      return AppConstants.POUND_SYMBOL;
    } else if (AppConstants.CURRENCY_CODE_EUR.equals(currencyCode)) {
      return AppConstants.EURO_SYMBOL;
    }
    return "";
  }
}
