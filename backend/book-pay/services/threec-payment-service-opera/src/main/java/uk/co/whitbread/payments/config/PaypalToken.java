package uk.co.whitbread.payments.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.model.threec.PaypalJwtClientTokenFingerPrintResponse;
import uk.co.whitbread.payments.model.threec.PaypalJwtClientTokenResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
@Slf4j
@Data
public class PaypalToken {
    private String token;
    private Instant tokenExpiryDateTime;
    private String clientId;
    private final ObjectMapper mapper;
    private static final long TOKENS_TIME_MS = 1000L;
    private static final int TOKENS_EXPIRY_TIME_MS = 30;
    private static final int TOKENS_PART_TWO = 1;
    private static final String TOKENS_REGEX = "\\.";

    private PaypalToken (){
        mapper = new ObjectMapper().findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    public PaypalToken(String country, String token) {
        this();
        this.token = token;
        decodePayPalToken(country, token);

    }

    private void decodePayPalToken(String country, String inputToken) {
        PaypalJwtClientTokenResponse PaypalJwtClientTokenResponse;
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            //Decoding the PayPal token to retrieve the header
            String header = new String(decoder.decode(inputToken));
            PaypalJwtClientTokenResponse = mapper.readValue(header, PaypalJwtClientTokenResponse.class);

            String authorizationFingerprint = PaypalJwtClientTokenResponse.getAuthorizationFingerprint();
            PaypalJwtClientTokenFingerPrintResponse paypalJwtClientTokenFingerPrintResponse;
            //Decoding the PayPal token part two to retrieve the authorizationFingerprint
            String[] fingerprintToken = authorizationFingerprint.split(TOKENS_REGEX);
            String tokenPartTwo = new String(decoder.decode(fingerprintToken[TOKENS_PART_TWO]));
            paypalJwtClientTokenFingerPrintResponse = mapper.readValue(tokenPartTwo, PaypalJwtClientTokenFingerPrintResponse.class);

            String expTime = paypalJwtClientTokenFingerPrintResponse.getExp();
            Date expDate = new Date(Integer.parseInt(expTime) * TOKENS_TIME_MS);
            Instant expDateinstant = Instant.ofEpochMilli(expDate.getTime());

            tokenExpiryDateTime = expDateinstant.minus(Duration.ofMinutes(TOKENS_EXPIRY_TIME_MS));
            clientId = PaypalJwtClientTokenResponse.getPaypal().getClientId();

        } catch (Exception ex) {
            log.error("Could not decode paypal token: ", ex);
            throw new IllegalArgumentException(String.format("Error encountered during creating paypal client token with country code %s.", country));
        }
    }
}
