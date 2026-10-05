package uk.co.whitbread.payments.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "paypal")
@Configuration
@Data
@Slf4j
public class PaypalConfig {
    private PaypalSuccessCodes successCodes;
    private String merchantID;
    private String publicKey;
    private String privateKey;
    private String environment;
    private String paypalTransactions;
    private List<PaypalAccountName> accountNames;
    private long timeout;
    private PaypalSensitiveData paypalSensitiveData;
    private PaypalConfigData paypalConfigData;
    private String url;
    private String method;
    private int expireAt;
    private int amount;
    private PaypalTestCard paypalTestCard;
}
