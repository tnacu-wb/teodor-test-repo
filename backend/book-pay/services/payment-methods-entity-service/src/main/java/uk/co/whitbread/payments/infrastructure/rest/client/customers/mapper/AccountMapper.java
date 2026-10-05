package uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payments.domain.model.out.Card;
import uk.co.whitbread.payments.domain.model.out.CustomerAccount;
import uk.co.whitbread.payments.domain.model.out.PaymentCard;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.CustomerAccountDto;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.PaymentCardDto;

@Mapper(componentModel = "spring", uses = AccountMapperTransformer.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AccountMapper {

  @Mapping(source = "expiryDate", target = "expiryMonth", qualifiedByName = "toExpiryMonth")
  @Mapping(source = "expiryDate", target = "expiryYear", qualifiedByName = "toExpiryYear")
  @Mapping(source = "cardType", target = "type")
  @Mapping(source = "cardNumber", target = "cardNumber")
  @Mapping(source = "cardToken", target = "token")
  @Mapping(source = "cardType", target = "cardName", qualifiedByName = "toCardName")
  Card toCardModel(PaymentCard card);

  @Mapping(source = "expiryDate", target = "expiryMonth", qualifiedByName = "toExpiryMonth")
  @Mapping(source = "expiryDate", target = "expiryYear", qualifiedByName = "toExpiryYear")
  @Mapping(source = "cardType", target = "type")
  @Mapping(target = "cardType", constant = "BUSINESS_CENTRALLY_STORED_CARD")
  @Mapping(source = "cardNumber", target = "cardNumber")
  @Mapping(source = "cardToken", target = "token")
  @Mapping(source = "cardType", target = "cardName", qualifiedByName = "toCardName")
  Card toCentralCardModel(PaymentCardDto cardDto);

  CustomerAccount toCustomerAccountModel(CustomerAccountDto customerAccountDto);
}