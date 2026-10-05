package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;


@Data
public class BillingAddressCaptRequestDto {

  @NotEmpty
  @Schema(example = "LONSTM")
  private String hotelId;

  @Schema(example = "PAY_NOW")
  private String paymentOption;

  @NotNull
  @Valid
  @Schema
  private List<String> reservationIds;

  @NotNull
  @Valid
  private BookerDetailsDto booker;

  private boolean updateGuestProfile;

  private boolean updateCompanyProfile;

  private boolean updateContactProfile;

  private String channel;
}