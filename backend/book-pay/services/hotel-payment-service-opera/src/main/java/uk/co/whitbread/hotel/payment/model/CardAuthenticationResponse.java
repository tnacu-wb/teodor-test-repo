package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardAuthenticationResponse {
    @Schema(example = "WZkGzYSWX9yfcWOb")
    private String sessionId;

    @Schema(example = "Y")
    private String transactionStatus;

    @Schema(example = "AUTHENTICATION_SUCCESSFUL")
    private String authenticationStatus;

    @Schema(example = "<div id=\"threedsFrictionLessRedirect\" xmlns=\"http://www.w3.org/1999/html\"> <iframe id=\"challengeFrame\" name=\"challengeFrame\"> </iframe> <form id=\"threedsFrictionLessRedirectForm\" method=\"POST\" action=\"https://www.google.co.uk\" target=\"challengeFrame\"> <input type=\"hidden\" name=\"order.id\" value=\"3500900087773322\" /> <input type=\"hidden\" name=\"transaction.id\" value=\"1\" /> <input type=\"hidden\" name=\"response.gatewayRecommendation\" value=\"PROCEED_WITH_PAYMENT\" /> <input type=\"hidden\" name=\"result\" value=\"SUCCESS\" /> </form> <script>document.getElementById(\"threedsFrictionLessRedirectForm\").submit();</script> </div>")
    private String redirectHtml;

    @Schema(example = "true")
    private boolean success;

}