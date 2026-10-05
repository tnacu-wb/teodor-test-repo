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
public class AcceptedCardTypeDto {

  @Schema(required = true,
      description = "CardDto type",
      implementation = CardType.class, example = "VI")
  private CardType type;

  private String name;

  @Schema(example = "https://www.visa.co.uk/dam/VCOM/regional/lac/ENG/Default/Partner%20With%20Us/Payment%20Technology/visapos/full-color-800x450.jpg", description = "Logo url for card type")
  private String logoSrc;

}
