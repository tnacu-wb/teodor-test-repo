package uk.co.whitbread.hotel.card.utils;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.model.PaymentCard;

import java.util.regex.Pattern;

@Component
public class PaymentCardMasker {

    private static final String MASKED_PATTERN = "^\\*[\\*]+[0-9]{4}$";

    public String maskNumber(String cardNumber) {
        if (cardNumber != null) {
            String s = cardNumber.replaceAll("\\D", "");
            int start = 0;
            int end = s.length() - 4;
            String overlay = StringUtils.repeat("*", end - start);
            cardNumber = StringUtils.overlay(s, overlay, start, end);
        }
        return cardNumber;
    }

    public boolean isMasked(String cardNumber){
        return cardNumber != null && Pattern.matches(MASKED_PATTERN, cardNumber);
    }

    public PaymentCard hideSensitiveInfoFromCard(PaymentCard paymentCard, boolean unmaskCardNumber) {
        if (unmaskCardNumber) {
            return paymentCard;
        }

        paymentCard.setCardNumber(maskNumber(paymentCard.getCardNumber()));
        paymentCard.setCnpBusinessAccountPassword(null);
        return paymentCard;
    }

}
