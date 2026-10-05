package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
	@Schema(example = "WZkGzYSWX9yfcWOb")
	private String sessionId;
	@Schema(example = "true")
	private Boolean threeDSecureRequired;
	@Schema(example = "eJxdUltvgjAUfvdXEH8Abbl5SW2i80EfMGYz2TMrJ9IpBQsMt1+/Fq2gTUj6XUpPv3PoIVMA6w/gjQI2chw" +
			"aQ1UlR3BEuhiXiRtOAn8yiWZjI2p5v3yHy22v0Q+oShSSERe7HkUWWjkGxbNE1pbQVMIvq+2OBXga+vrEHfZ6Dmq7ZmGAAz8ifuARgr" +
			"H23ejeJpMc2Gcm6i8FSUpRh3uZF42s1S+behFFFvRyo84sq+tyjlDbtm6pIBeghJQuL3KKjGyfgF7fQPeNIarhdVeRsni9bIff7sBx/" +
			"Mf93fdpQZFx9P40qYF5mESEkNAhwZzM5gRT1PGDrHJTOSPh1MVavcNeL00ly4fJeIbUII5GKZDc5mFRb4BrWUjQZ3TUj/2gXqg4299S" +
			"crZSOpuihrOzKoqTkEddt9Efib0GRN82TwPAa93KQC9fL08vnQIxo9AJT0UJ3TYyxaSrSvQ9pMj+U19nJ9L0qptdNqLoea7/AYmoydQ=")
	private String authenticationToken;

	@Schema(example = "https://some.url.com/acs")
	private String redirectURL;

    private Boolean prepaymentNotRequired;

	@Schema(description = "If present, signifies a 3DS v2.1 journey.", example = "<div id=\"initiate3dsSimpleRedirect\" xmlns=\"http://www.w3.org/1999/html\"> <script>console.info(\"Simulator method call completed.\");</script></div>\n")
	private String redirectHtml;
}
