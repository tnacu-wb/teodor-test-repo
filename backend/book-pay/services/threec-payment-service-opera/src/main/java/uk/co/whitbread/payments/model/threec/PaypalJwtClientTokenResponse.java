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
public class PaypalJwtClientTokenResponse {

    @JsonProperty(value = "token")
    String token;
    @JsonProperty(value = "version")
    String version;
    @JsonProperty(value = "authorizationFingerprint")
    String authorizationFingerprint;
    @JsonProperty(value = "configUrl")
    String configUrl;
    @JsonProperty(value = "merchantAccountId")
    String merchantAccountId;
    @JsonProperty(value = "graphQL")
    private GraphQL graphQL;
    @JsonProperty(value = "clientApiUrl")
    String clientApiUrl;
    @JsonProperty(value = "environment")
    String environment;
    @JsonProperty(value = "merchantId")
    String merchantId;
    @JsonProperty(value = "assetsUrl")
    String assetsUrl;
    @JsonProperty(value = "authUrl")
    String authUrl;
    @JsonProperty(value = "venmo")
    String venmo;
    @JsonProperty(value = "challenges")
    List<String> challenges;
    @JsonProperty(value = "threeDSecureEnabled")
    Boolean threeDSecureEnabled;
    @JsonProperty(value = "analytics")
    private Analytics analytics;
    @JsonProperty(value = "paypalEnabled")
    Boolean paypalEnabled;
    @JsonProperty(value = "paypal")
    private Paypal paypal;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GraphQL {
        @JsonProperty(value = "url")
        String url;
        @JsonProperty(value = "date")
        String date;
        @JsonProperty(value = "features")
        List<String> features;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Analytics {
        @JsonProperty(value = "url")
        String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Paypal {
        @JsonProperty(value = "billingAgreementsEnabled")
        Boolean billingAgreementsEnabled;
        @JsonProperty(value = "environmentNoNetwork")
        Boolean environmentNoNetwork;
        @JsonProperty(value = "unvettedMerchant")
        Boolean unvettedMerchant;
        @JsonProperty(value = "allowHttp")
        Boolean allowHttp;
        @JsonProperty(value = "displayName")
        String displayName;
        @JsonProperty(value = "clientId")
        String clientId;
        @JsonProperty(value = "baseUrl")
        String baseUrl;
        @JsonProperty(value = "assetsUrl")
        String assetsUrl;
        @JsonProperty(value = "directBaseUrl")
        String directBaseUrl;
        @JsonProperty(value = "environment")
        String environment;
        @JsonProperty(value = "braintreeClientId")
        String braintreeClientId;
        @JsonProperty(value = "merchantAccountId")
        String merchantAccountId;
        @JsonProperty(value = "currencyIsoCode")
        String currencyIsoCode;
    }

}
