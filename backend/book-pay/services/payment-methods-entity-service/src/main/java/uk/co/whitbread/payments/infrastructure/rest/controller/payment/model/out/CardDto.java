package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDto {

  @Schema(description = "3C Payment token.", example = "4943056398164344242")
  private String token;

  @Schema(description = "Card number masked", example = "************1103")
  private String cardNumber;

  @Schema(description = "Expiry month for the payment card/token.", example = "01", format = "MM")
  private String expiryMonth;

  @Schema(description = "Expiry year for the payment card/token.", example = "21", format = "YY")
  private String expiryYear;

  @Schema(required = true, description = "CardDto type",
      implementation = CardType.class, example = "VI")
  private CardType type;

  @Schema(description = "The name of the card type.", example = "Visa Credit")
  private String cardName;

  @Schema(example = "https://www.visa.co.uk/dam/VCOM/regional/lac/ENG/Default/Partner%20With%20Us/Payment%20Technology/visapos/full-color-800x450.jpg", description = "Logo url for card type")
  private String logoSrc;

  @Schema(description = "Name of the card holder", example = "FirstName LastName")
  private String cardHolderName;

  @Schema(description = "Type of saved card", implementation = StoredCardType.class,
      example = "BUSINESS_CENTRALLY_STORED_CARD")
  private String cardType;

  private boolean cnpRequired;

}
