package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaypalJwtClientTokenFingerPrintResponse {

    @JsonProperty(value = "exp")
    String exp;
    @JsonProperty(value = "jti")
    String jti;
    @JsonProperty(value = "sub")
    String sub;
    @JsonProperty(value = "iss")
    String iss;
    @JsonProperty(value = "merchant")
    private Merchant merchant;
    @JsonProperty(value = "rights")
    List<String> rights;
    @JsonProperty(value = "scope")
    List<String> scope;
    @JsonProperty(value = "options")
    private Options options;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Merchant {
        @JsonProperty(value = "public_id")
        String publicId;
        @JsonProperty(value = "verify_card_by_default")
        Boolean verifyCardByDefault;
        @JsonProperty(value = "verify_wallet_by_default")
        Boolean verifyWalletByDefault;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Options {
        @JsonProperty(value = "merchant_account_id")
        String merchantAccountId;
    }

}
