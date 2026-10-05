package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ThreeCResponse {

    @JsonProperty(value = "iPageHtml")
    private String iPageHtml;
    private String sessionId;
    private String template;
    private String providerUrl;
    private String providerStatus;
    private String providerStatusText;
    private String providerReason;
    private String providerResult;
    private String authCode;
    private String last4Digits;
    private String cardSchemeId;
    private String cardSchemeName;
    private String token;
    private String binRange;
    private String expiry;
    private String cardholderFirstName;
    private String cardholderLastName;
    private String status;
    private String avsResult;
    private String fraudCheckDecision;
    private String fraudCheckResult;
    private String fraudCheckResultReason;
    private String fraudCheckRequestId;

    /**
     * Date that the webhook was received
     */
    private String date;

    /**
     * Time that the webhook was received
     */
    private String time;

    /**
     * This is the Transaction reference returned by the
     * issuer/acquirer in the response to the initial SCA authorization response
     * message. This is the card scheme reference of the agreement. Not all card
     * schemes will return such a reference, but whenever present this reference
     * should be saved by the merchant POS/PMS. The merchant POS/PMS will
     * send this 3CXml data field for any further MIT to perform under this agreement,
     * so that it can be forwarded to the issuer/acquirer.
     */
    private String scaReference;
    /**
     * 0 No 3DS used
     * 1 Full authentication (ECI = 2 or 5 dependent on card scheme)
     * 2 Standing (3DS process but ECI was e.g. 6 or 7)
     * 3 Proceeded without 3DS (Based on proceed with no enrolment / proceed on error flags)
     */
    private String threeDSIndicator;
}
