package uk.co.whitbread.payments.properties;

import lombok.Setter;

import java.util.Optional;

@Setter
public class AccountConfigProperties extends BaseAccountConfigProperties {

    public static final String DEFAULT_FRAUD_MODE = "5";
    public static final String DEFAULT_VERSION = "W2MXG520";
    public static final String DEFAULT_OPTIONS_FLAG = "G";
    public static final String DEFAULT_CARD_ON_FILE_INDICATOR = "S";
    public static final String DEFAULT_TRANS_INITIATOR = "M";

    private String fraudMode;
    private String optionsFlag;
    private String version;
    private String cardOnFileIndicator;
    private String transInitiator;

    public String getFraudMode() {
        return Optional.ofNullable(fraudMode).orElse(DEFAULT_FRAUD_MODE);
    }

    public String getOptionsFlag(){
        return Optional.ofNullable(optionsFlag).orElse(DEFAULT_OPTIONS_FLAG);
    }

    public String getVersion() {
        return Optional.ofNullable(version).orElse(DEFAULT_VERSION);
    }

    public String getCardOnFileIndicator() {
        return Optional.ofNullable(cardOnFileIndicator).orElse(DEFAULT_CARD_ON_FILE_INDICATOR);
    }

    public String getTransInitiator() {
        return Optional.ofNullable(transInitiator).orElse(DEFAULT_TRANS_INITIATOR);
    }
}

