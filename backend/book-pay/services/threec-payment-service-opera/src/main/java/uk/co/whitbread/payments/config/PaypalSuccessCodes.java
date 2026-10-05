package uk.co.whitbread.payments.config;

import lombok.Data;

@Data
public class PaypalSuccessCodes {
    private String result;
    private String trxState;
    private String fraudInfoDecision;
}
