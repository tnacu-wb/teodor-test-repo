package uk.co.whitbread.hotel.account.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardNumberMapper {

  @Named("maskCardNumber")
  default String maskCardNumber(String cardNumber) {
    if(cardNumber == null ){
      return null;
    }
    String last4Digits = cardNumber.substring(cardNumber.length() - 4);
    return StringUtils.repeat("*", cardNumber.length() - 4) + last4Digits;
  }
}
