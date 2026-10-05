package uk.co.whitbread.hotel.card.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.card.generated.models.payments.SaveCardRequestDto;
import uk.co.whitbread.hotel.card.model.PaymentCardBBCentral;
import uk.co.whitbread.hotel.card.model.SaveCardRequest;

@Mapper(componentModel = "spring")
public interface SaveCardMapper {

  @Mapping(target = "cardType", source = "cardDetails.cardType")
  @Mapping(target = "cardId", source = "cardDetails.cardId")
  @Mapping(target = "cardLabel", source = "cardDetails.cardLabel")
  @Mapping(target = "cnpRequired", source = "cardDetails.cnpRequired")
  @Mapping(target = "personalCard", source = "cardDetails.personalCard")
  @Mapping(target = "business", source = "cardDetails.business")
  @Mapping(target = "memorableWord", source = "cardDetails.memorableWord")
  @Mapping(target = "billingAddress.postalCode", source = "billingAddress.postCode")
  SaveCardRequestDto toSaveCardRequest(SaveCardRequest saveCardRequest);

  @Mapping(target = "companyId", source = "companyAccountId")
  @Mapping(target = "userEmail", source = "email")
  @Mapping(target = "billingAddress.postCode", source = "billingAddress.postalCode")
  @Mapping(target = "cnpBusinessAccountPassword", source = "memorableWord")
  PaymentCardBBCentral toPaymentCardBBCentral(SaveCardRequestDto saveCardRequest);
}
