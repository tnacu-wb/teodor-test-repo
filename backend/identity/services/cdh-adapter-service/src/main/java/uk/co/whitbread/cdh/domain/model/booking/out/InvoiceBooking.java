package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceBooking {

  @JsonProperty("BookingReference")
  private String bookingReference;

  @JsonProperty("ConfirmationNumber")
  private String confirmationNumber;

  @JsonProperty("ReservationId")
  private String reservationId;

  @JsonProperty("RoomNumber")
  private String roomNumber;

  @JsonProperty("ArrivalDate")
  private String arrivalDate;

  @JsonProperty("DepartureDate")
  private String departureDate;

  @JsonProperty("DCTMCUserNAme")
  private String dctmcUserNAme;

  @JsonProperty("IATANumber")
  private Integer iataNumber;

  @JsonProperty("IATANumberString")
  private String iataNumberString;

  @JsonProperty("PartnerId")
  private String partnerId;

  @JsonProperty("ExternalReferences")
  private InvoiceExternalReferences externalReferences;

  @JsonProperty("BillingContact")
  private InvoiceBillingContact billingContact;

  @JsonProperty("Guest")
  private List<InvoiceGuest> guest;
}

