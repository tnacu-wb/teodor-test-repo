package uk.co.whitbread.company.utils;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PaymentCardMasker {

    private static final String MASK_CHAR = "*";

    public String maskNumber(String cardNumber) {
        if (cardNumber != null) {
            String s = cardNumber.replaceAll("\\D", "");
            int start = 0;
            int end = s.length() - 4;
            String overlay = StringUtils.repeat(MASK_CHAR, end - start);
            cardNumber = StringUtils.overlay(s, overlay, start, end);
        }
        return cardNumber;
    }
}
