package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import java.util.List;
import lombok.Data;


@Data
public class CustomerAccountDto {
  private List<AdditionalGuestDto> additionalGuests;
  private BookingPreferenceDto bookingPreference;
  private BusinessDto business;
  private Boolean businessUse;
  private String companyId;
  private String companyName;
  private ContactDetailDto contactDetail;
  private String guestHistoryNumber;
  private PaymentPreferenceDto paymentPreference;
  private String sessionId;
}
