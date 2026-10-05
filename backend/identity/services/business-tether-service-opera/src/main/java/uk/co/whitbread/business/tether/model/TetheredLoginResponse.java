package uk.co.whitbread.business.tether.model;

import lombok.Data;

@Data
public class TetheredLoginResponse {
    private String errorCode;
    private String errorDescription;
    private String hash;
    private String sessionId;
    private String sharedSecret;
    private String nonce;
    private String timestamp;
}
