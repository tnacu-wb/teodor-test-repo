package uk.co.whitbread.company.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.company.model.PaymentCard;
import uk.co.whitbread.shared.cdh.model.company.CompanyPaymentCard;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

  @Mapping(target = "billingAddress.country", source = "billingAddress.countryCode")
  @Mapping(target = "cardToken", source = "token")
  PaymentCard toPaymentCard(CompanyPaymentCard companyPaymentCard);

  default List<PaymentCard> toPaymentCards(List<CompanyPaymentCard> companyPaymentCards) {
    if (companyPaymentCards == null) {
      return Collections.emptyList();
    }

    return companyPaymentCards.stream()
        .filter(companyPaymentCard -> !companyPaymentCard.isDeleted())
        .map(this::toPaymentCard)
        .toList();
  }


}
