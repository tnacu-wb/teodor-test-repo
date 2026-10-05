package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentCard;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in.PaymentEckohCardDto;

@Mapper(componentModel = "spring")
public interface PaymentMethodOhipMapper {

  String CARD_NO_MASK = "XXXXXXXXXXXX";
  String PIBA_UK_CARD_TYPE = "BU";
  String PIBA_EURO_CARD_TYPE = "BD";

  @Mapping(source = "token", target = "cardNumber")
  @Mapping(expression = "java(toCardNoMaskedModel(peymentCard))", target = "cardNumberMasked")
  @Mapping(expression = "java(toUserDefinedCardTypeModel(peymentCard))", target = "userDefinedCardType")
  @Mapping(expression = "java(toCardTypeModel(peymentCard))", target = "cardType")
  ResPaymentCardType toModel(PaymentCard peymentCard);

  @Mapping(source = "token", target = "cardNumber")
  @Mapping(expression = "java(toEckohCardNoMaskedModel(peymentCard))", target = "cardNumberMasked")
  @Mapping(expression = "java(toEckohUserDefinedCardTypeModel(peymentCard))", target = "userDefinedCardType")
  @Mapping(expression = "java(toEckohCardTypeModel(peymentCard))", target = "cardType")
  ResPaymentCardType toEckohModel(PaymentEckohCardDto peymentCard);

  default String toUserDefinedCardTypeModel(PaymentCard paymentCard) {
    return getUserDefinedCardType(paymentCard.getCardType());
  }

  default CardTypeType toCardTypeModel(PaymentCard paymentCard) {
    return getCardType(paymentCard.getCardType());
  }

  default String toEckohUserDefinedCardTypeModel(PaymentEckohCardDto paymentCard) {
    return getUserDefinedCardType(paymentCard.getCardType());
  }

  default CardTypeType toEckohCardTypeModel(PaymentEckohCardDto paymentCard) {
    return getCardType(paymentCard.getCardType());
  }

  default String toCardNoMaskedModel(PaymentCard paymentCard) {
    return CARD_NO_MASK.concat(paymentCard.getCardNumberLast4Digits());
  }

  default String toEckohCardNoMaskedModel(PaymentEckohCardDto paymentCard) {
    Objects.requireNonNull(paymentCard.getMaskedPan());
    return CARD_NO_MASK.concat(extractLast4Digits(paymentCard));
  }

  private String extractLast4Digits(PaymentEckohCardDto paymentCard) {
    return paymentCard.getMaskedPan()
        .substring(paymentCard.getMaskedPan().length() - 4);
  }

  private String getUserDefinedCardType(String cardType) {
    if (PIBA_UK_CARD_TYPE.equals(cardType)) {
      return PIBA_UK_CARD_TYPE;
    } else if (PIBA_EURO_CARD_TYPE.equals(cardType)) {
      return PIBA_EURO_CARD_TYPE;
    }

    return null;
  }

  private CardTypeType getCardType(String cardType) {
    if (Objects.isNull(cardType) || PIBA_UK_CARD_TYPE.equals(cardType) || PIBA_EURO_CARD_TYPE.equals(cardType)) {
      return null;
    }

    return Enum.valueOf(CardTypeType.class, cardType);
  }
}
