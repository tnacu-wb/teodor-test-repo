package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceBookingDto {

  private String bookingReference;
  private String confirmationNumber;
  private String reservationId;
  private String roomNumber;
  private String arrivalDate;
  private String departureDate;
  private String dctmcUserNAme;
  private Integer iataNumber;
  private String iataNumberString;
  private String partnerId;
  private InvoiceExternalReferencesDto externalReferences;
  private InvoiceBillingContactDto billingContact;
  private List<InvoiceGuestDto> guest;
}
