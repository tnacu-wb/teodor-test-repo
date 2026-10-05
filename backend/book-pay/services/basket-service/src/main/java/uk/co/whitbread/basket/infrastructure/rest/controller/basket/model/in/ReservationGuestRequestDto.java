package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.generated.models.reservation.BookerDetailsDto;
import uk.co.whitbread.basket.generated.models.reservation.StayingGuestDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationGuestRequestDto {
  @JsonProperty("basketReference")
  private String basketReference;

  @JsonProperty("booker")
  private BookerDetailsDto booker;

  @JsonProperty("hotelId")
  private String hotelId;

  @JsonProperty("reasonForStay")
  private String reasonForStay;

  @JsonProperty("sendEmailConfirmation")
  private Boolean sendEmailConfirmation;

  @JsonProperty("sendEmailInvoice")
  private Boolean sendEmailInvoice;

  @JsonProperty("stayingGuests")
  @Valid
  private List<StayingGuestDto> stayingGuests = new ArrayList<>();

  @JsonProperty("bookerProfileId")
  private String bookerProfileId;

  @JsonProperty("companyProfileId")
  private String companyProfileId;
}
