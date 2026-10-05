package uk.co.whitbread.reservation.domain.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ReservationRequest implements SelfValidation<ReservationRequest> {

  @NotEmpty
  @Size(min = 1)
  @Valid
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Reservation> reservations;

  @NotNull
  @Valid
  private BookingChannel bookingChannel;

  @Valid
  private boolean getReservationsByIds;

  private String token;
  private String ratePlanCode;
  private String distributionIATANumber;

  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private List<String> bookingNotes;
  private List<String> specialRequests;

  private Boolean isOta;
  private Boolean fieldRate;
  private String bookingFlowId;
  private String reasonForStay;
}
