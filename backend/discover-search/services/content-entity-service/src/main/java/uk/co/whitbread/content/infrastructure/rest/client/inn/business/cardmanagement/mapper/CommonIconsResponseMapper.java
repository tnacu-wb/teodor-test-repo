package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CommonIcons;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CommonIconsResponseAemDto;

@Mapper(componentModel = "spring")
public interface CommonIconsResponseMapper {

  @Mapping(source = "paymentVisa", target = "payment.visa")
  @Mapping(source = "chevronUp", target = "chevron.up")
  @Mapping(source = "notificationError", target = "notification.error")
  @Mapping(source = "chevronRight", target = "chevron.right")
  @Mapping(source = "arrowRight", target = "arrow.right")
  @Mapping(source = "arrowUp", target = "arrow.up")
  @Mapping(source = "arrowLeft", target = "arrow.left")
  @Mapping(source = "arrowDown", target = "arrow.down")
  @Mapping(source = "paymentPiba", target = "payment.piba")
  @Mapping(source = "paymentAmex", target = "payment.amex")
  @Mapping(source = "paymentPibaEuro", target = "payment.pibaEuro")
  @Mapping(source = "chevronRightPurple", target = "chevron.rightPurple")
  @Mapping(source = "chevronLeftPurple", target = "chevron.leftPurple")
  @Mapping(source = "chevronUpPurple", target = "chevron.upPurple")
  @Mapping(source = "chevronDownPurple", target = "chevron.downPurple")
  @Mapping(source = "notificationInfo", target = "notification.info")
  @Mapping(source = "notificationQuestion", target = "notification.question")
  @Mapping(source = "chevronLeft", target = "chevron.left")
  @Mapping(source = "paymentMastercard", target = "payment.mastercard")
  @Mapping(source = "notificationSuccess", target = "notification.success")
  @Mapping(source = "chevronDown", target = "chevron.down")
  @Mapping(source = "notificationAlert", target = "notification.alert")
  CommonIcons toModel(CommonIconsResponseAemDto commonIconsResponseAemDto);

}
