package uk.co.whitbread.hotel.account.client.payment;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.account.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.account.exceptions.EmployeeUpdate500Exception;

@Slf4j
@Component
public class Payment3CPFallbackFactory implements FallbackFactory<Payment3CP> {

    @Override
    public Payment3CP create(Throwable throwable) {
        return createTokenRequest -> {
            log.warn("Failed to call 'threecPayment' (circuit breaker is open) when updating payment for " +
                    "createTokenRequest={}", maskCreateTokenRequest(createTokenRequest));

            throw new EmployeeUpdate500Exception(throwable.getMessage());
        };
    }

    public static String maskCreateTokenRequest(CreateTokenRequest request) {
        if (request == null) {
            return "null";
        }
        String cardNumber = Optional.ofNullable(request.getCardNumber())
            .map(num -> {
                String digits = num.replaceAll("\\D", "");
                if (digits.length() <= 4) return digits;
                int maskLength = digits.length() - 4;
                String overlay = StringUtils.repeat("*", maskLength);
                return StringUtils.overlay(digits, overlay, 0, maskLength);
            })
            .orElse("*****");

        return "CreateTokenRequest{requestId=" + request.getRequestId() +
            ", cardNumber=" + cardNumber +
            ", expiryMonth=" + request.getExpiryMonth() +
            ", expiryYear=" + request.getExpiryYear() +
            ", cardHolder=" + request.getCardHolder() +
            "}";
    }

}
