package uk.co.whitbread.payments.config;

import lombok.Data;

@Data
public class PaypalSensitiveData {
    private String type;
    private String zeroAuthType;
    private String Version;
    private String validationCode;
    private String validationCodeHash;
    private String optionFlags;
    private String cofIndicator;
    private String transInitiator;
    private String scaTransRef;
    private String mitCofIndicator;
}


