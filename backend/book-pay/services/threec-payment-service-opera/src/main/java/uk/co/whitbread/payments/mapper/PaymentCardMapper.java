package uk.co.whitbread.payments.mapper;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.payments.model.Address;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.PaymentType;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.card.AddressDTO;
import uk.co.whitbread.payments.model.card.PaymentCardDTO;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

  @Mapping(target = "cardId", source = "saveCardDetails.cardId")
  @Mapping(target = "cardLabel", source = "saveCardDetails.cardLabel")
  @Mapping(target = "business", source = "saveCardDetails.business")
  @Mapping(target = "personalCard", source = "saveCardDetails.personalCard")
  @Mapping(target = "cnpRequired", source = "saveCardDetails.cnpRequired")
  @Mapping(target = "cnpBusinessAccountPassword", source = "saveCardDetails.memorableWord")
  @Mapping(target = "customerAccountId", source = "saveCardDetails.accountId")
  @Mapping(target = "companyAccountId", source = "saveCardDetails.companyAccountId")
  @Mapping(target = "employeeAccountId", source = "saveCardDetails.employeeAccountId")
  @Mapping(target = "userEmail", source = "saveCardDetails.email")
  @Mapping(target = "cardType", source = "providerResponse.threeCResponse.cardSchemeId")
  @Mapping(target = "cardToken", source = "providerResponse.threeCResponse.token")
  @Mapping(target = "cardHolderName", source = "providerResponse.threeCResponse", qualifiedByName = "toCardHolderName")
  @Mapping(target = "cardNumberLast4Digits", source = "providerResponse.threeCResponse.last4Digits")
  @Mapping(target = "expiryDate", source = "providerResponse.threeCResponse.expiry")
  @Mapping(target = "billingAddress", source = "saveCardDetails.billingAddress", qualifiedByName = "toAddressDTO")
  PaymentCardDTO toPaymentCardDTO(PaymentResponse paymentResponse);

  @Named("toAddressDTO")
  @Mapping(target = "postCode", source = "postalCode")
  AddressDTO toAddressDTO(Address address);

  @Named("toCardHolderName")
  default String toCardHolderName(ThreeCResponse threeCResponse) {
    var decoder = Base64.getDecoder();
    var firstName = new String(decoder.decode(threeCResponse.getCardholderFirstName()), UTF_8);
    if (threeCResponse.getCardSchemeName() == null
        || !PaymentType.PIBA.name().equalsIgnoreCase(threeCResponse.getCardSchemeName())) {
      var lastName = new String(decoder.decode(threeCResponse.getCardholderLastName()), UTF_8);
      return firstName + " " + lastName;
    }
    return firstName;
  }
}