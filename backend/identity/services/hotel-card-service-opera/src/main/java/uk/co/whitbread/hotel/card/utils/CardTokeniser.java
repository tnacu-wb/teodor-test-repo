package uk.co.whitbread.hotel.card.utils;

import com.google.gson.Gson;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.client.payment.Payment3CP;
import uk.co.whitbread.hotel.card.client.payment.model.CardHolder;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenResponse;
import uk.co.whitbread.hotel.card.exceptions.ThreeCPClientException;
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.PaymentCard;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardTokeniser {

    private final Payment3CP payment3CP;

    public void tokeniseCard(PaymentCard paymentCard, String email) {
        if (paymentCard.getCardNumber().matches("^\\*+\\d{4}$") ) {
            return;
        }
        try {
            CreateTokenRequest tokenRequest = createCreateTokenRequest(paymentCard, email);
            CreateTokenResponse createTokenResponse = payment3CP.createToken(tokenRequest);
            paymentCard.setCardToken(createTokenResponse.getToken());
            paymentCard.setCardType(createTokenResponse.getCardType());
        } catch (Exception e) {
            log.error("Tokenization error: " + e.getMessage());
            throw new ThreeCPClientException(extractThreeCpErrorMessage(e));
        }
    }

    private static CreateTokenRequest createCreateTokenRequest(PaymentCard paymentCard, String email) {
        String expiryDate = paymentCard.getExpiryDate();
        String expiryMonth = expiryDate.substring(0, 2);
        String expiryYear = expiryDate.substring(2);
        Address address = paymentCard.getBillingAddress();

        return CreateTokenRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .cardNumber(paymentCard.getCardNumber())
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .cardHolder(
                        CardHolder.builder()
                                .address(
                                        uk.co.whitbread.hotel.card.client.payment.model.Address.builder()
                                                .line1(address.getLine1())
                                                .countryCode(address.getCountryCode())
                                                .postalCode(address.getPostCode())
                                                .build()
                                )
                                .email(email)
                                .cardHolderName(paymentCard.getCardHolderName())
                                .build()
                ).build();
    }

    private String extractThreeCpErrorMessage(Exception e) {
        String errorMessage = e.getMessage();
        if (!errorMessage.contains("{")) {
            return "Something went wrong, please try again later";
        }
        String jsonString = errorMessage.substring(errorMessage.indexOf("{"), errorMessage.lastIndexOf("}") + 1);
        Map<String, String> json = new Gson().fromJson(jsonString, Map.class);
        return json.get("message");
    }
}
