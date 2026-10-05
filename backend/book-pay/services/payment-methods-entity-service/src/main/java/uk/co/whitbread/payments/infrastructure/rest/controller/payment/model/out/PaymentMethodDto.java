package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsFlow;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethodDto {

  @Schema(example = "CARD/PIBA", description = "Payment Method Name - CardDto/PIBA")
  @NotNull
  private String name;

  @Schema(example = "NEW_CARD", description = "CardDto type", implementation = CardOption.class)
  @NotNull
  private CardOption type;

  @Schema(example = "PIBAGB/PIBADE", description = "Subtype")
  private String subType;

  @Schema(example = "1", description = "Order(priority) of payment method")
  @NotNull
  private int order;

  @Schema(description = "Logo source for each payment type")
  private String logoSrc;

  @Schema(description = "Accepted card types")
  private List<AcceptedCardTypeDto> acceptedCardTypes;

  @Schema(description = "Saved CardDto data")
  private CardDto card;

  @Schema(description = "Payment Options available for the payment method")
  private List<PaymentOptionDto> paymentOptions;

  @Schema(example = "true", description = "Payment method enabled")
  private boolean enabled;

  @Schema(example = "true",
      description = "Determines if card holder present option has been pre selected. Saved Business Booker cards only.")
  private boolean cnpPreSelected = false; //TODO Set from bart

  @Schema(example = "true", description = "Determines if card holder present option is available")
  private boolean cnpOptionAvailable = false;

  @Schema(example = "CARD_NOT_ACCEPTED_AT_HOTEL",
      description = "why a saved card is not available.", implementation = CardStatus.class)
  private List<CardStatus> reasons = new ArrayList<>();

  @Schema(description = "BookingAllowancesDto data")
  private BookingAllowancesDto bookingAllowances;

  @Schema(description = "Client ID if applicable for the Payment Type")
  private String clientId;

  @Schema(description = "Token generated if applicable for the Payment Type")
  private String clientToken;

  @Schema(description = "Optional field, provides additional information about the payment method flow, "
      + "e.g. CheckInOnline ")
  private PaymentMethodsFlow flow;

  @Schema(description = "Indicates which payment provider backs the accepted card types for this method",
      implementation = PaymentProviderType.class)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private PaymentProviderType paymentProvider;

}
