package uk.co.whitbread.basket.domain.logic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CardCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.UpdateTokenRequest;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CcuiPaymentDomainMapper {

  @Mapping(target = "cardHolderFirstName", source = "card.cardHolderFirstName")
  @Mapping(target = "cardHolderLastName", source = "card.cardHolderLastName")
  @Mapping(target = "cardHolderAddress.line1", source = "card.cardHolderAddress.line1")
  @Mapping(target = "cardHolderAddress.line2", source = "card.cardHolderAddress.line2")
  @Mapping(target = "cardHolderAddress.line3", source = "card.cardHolderAddress.line3")
  @Mapping(target = "cardHolderAddress.line4", source = "card.cardHolderAddress.line4")
  @Mapping(target = "cardHolderAddress.countryCode", source = "card.cardHolderAddress.countryCode")
  @Mapping(target = "cardHolderAddress.postalCode", source = "card.cardHolderAddress.postalCode")
  @Mapping(target = "token", source = "token")
  @Mapping(target = "requestId", source = "requestId")
  UpdateTokenRequest toUpdateTokenRequest(CardCcui card, String token, String requestId);
}
