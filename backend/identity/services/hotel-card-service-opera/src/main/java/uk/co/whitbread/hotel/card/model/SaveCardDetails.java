package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveCardDetails {

  @NotEmpty
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private CardType cardType;
  private String cardId;
  private String cardLabel;
  private boolean cnpRequired;
  private boolean personalCard;
  private boolean business;
  private String memorableWord;
}
