package uk.co.whitbread.payments.config;

import lombok.Data;

@Data
public class PaypalConfigData {
    private String name;
    private String url;
    private String body;
    private String type;
    private Boolean enablePaypalConfigData;
    private String paypalProdConfigName;
}
