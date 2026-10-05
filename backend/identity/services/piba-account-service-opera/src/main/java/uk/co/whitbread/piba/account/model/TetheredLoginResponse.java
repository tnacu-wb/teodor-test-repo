package uk.co.whitbread.piba.account.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredLoginResponse {
	
	private String errorCode;
	private String errorDescription;
	private String hash;
	private String sessionId;
	private String sharedSecret;
	private String nonce;
	private String timestamp;
}