package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SaveCardRequest extends BaseRequest {
  @NotEmpty
  private String cardType;
  @NotNull
  private Address billingAddress;
  @Schema(description = "Is the card for business use - true for BB flow, false for MyPI flow; Defaults to false")
  private boolean business;
  @Schema(description = "Card identifier for centrally stored cards")
  private String cardId;
  @Schema(description = "A user-defined label for easy recognition of centrally stored cards")
  private String cardLabel;
  @Schema(description = "Card Not Present (CNP) memorable word for PIBA")
  private String memorableWord;
  @Schema(description = "Indicates whether it's a CNP context; Defaults to false")
  private boolean cnpRequired;
  @Schema(description = "True for personal cards (MyPI and BB), false for centrally stored cards (BB); Defaults to false")
  private boolean personalCard;
  @NotEmpty
  @Schema(description = "Environment hostname")
  private String environment;
  @Schema(description = "Authenticated user email")
  private String email;
  @Schema(description = "Authenticated account id")
  private String accountId;
  @Schema(description = "Authenticated company account id")
  private String companyAccountId;
  @Schema(description = "Authenticated employee account id")
  private String employeeAccountId;
  @Schema(description = "Language specified by the user")
  private String language;
  @Schema(description = "Website location of the user", defaultValue = "gb")
  private String country = "gb";
}
