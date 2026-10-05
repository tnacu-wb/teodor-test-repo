package uk.co.whitbread.hotel.card.mapper;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.card.model.PaymentCardBBCentral;
import uk.co.whitbread.hotel.card.model.PaymentCardBBPersonal;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardPIPersonal;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {
    String MASKED_CARD_PREFIX = "************";

    @Mapping(target = "cardNumber", source = "request.cardNumberLast4Digits",
            qualifiedByName = "last4DigitsToCardNumber")
    @Mapping(target = "cnpBusinessAccountUsername", source = "request",
            qualifiedByName = "cnpUsername")
    @Mapping(target = "cnpBusinessAccountPassword", source = "request",
            qualifiedByName = "cnpPassword")
    @Mapping(target = "billingAddress.companyName", ignore = true)
    @Mapping(target = "cardHolderName", source = "request.cardHolderName",
        qualifiedByName = "trimCardHolderName")
    PaymentCardPIPersonal toPaymentCardPIPersonal(PaymentCardDTO request);

    @Mapping(target = "cardNumber", source = "request.cardNumberLast4Digits",
            qualifiedByName = "last4DigitsToCardNumber")
    @Mapping(target = "cnpBusinessAccountUsername", source = "request",
            qualifiedByName = "cnpUsername")
    @Mapping(target = "cnpBusinessAccountPassword", source = "request",
            qualifiedByName = "cnpPassword")
    @Mapping(target = "cardHolderName", source = "request.cardHolderName",
        qualifiedByName = "trimCardHolderName")
    PaymentCardBBPersonal toPaymentCardBBPersonal(PaymentCardDTO request);

    @Mapping(target = "cardNumber", source = "request.cardNumberLast4Digits",
            qualifiedByName = "last4DigitsToCardNumber")
    @Mapping(target = "companyId", source = "companyAccountId")
    @Mapping(target = "cnpBusinessAccountUsername", source = "request",
            qualifiedByName = "cnpUsername")
    @Mapping(target = "cnpBusinessAccountPassword", source = "request",
            qualifiedByName = "cnpPassword")
    @Mapping(target = "cardHolderName", source = "request.cardHolderName",
        qualifiedByName = "trimCardHolderName")
    PaymentCardBBCentral toPaymentCardBBCentral(PaymentCardDTO request);

    @Named("last4DigitsToCardNumber")
    default String last4DigitsToCardNumber(String cardNumberLast4Digits) {
        return MASKED_CARD_PREFIX + cardNumberLast4Digits;
    }

    @Named("cnpUsername")
    default String cnpUsername(PaymentCardDTO request) {
        return Boolean.TRUE.equals(request.getCnpRequired()) ? request.getCnpBusinessAccountUsername() : null;
    }

    @Named("cnpPassword")
    default String cnpPassword(PaymentCardDTO request) {
        return Boolean.TRUE.equals(request.getCnpRequired()) ? request.getCnpBusinessAccountPassword() : null;
    }

    @Named("trimCardHolderName")
    default String trimCardHolderName(String cardHolderName) {
        return Optional.ofNullable(cardHolderName)
            .map(String::trim)
            .orElse(null);
    }
}
