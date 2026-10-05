package uk.co.whitbread.payments.domain.model.out;

import java.util.List;
import lombok.Data;

@Data
public class CustomerAccount {
  private List<AdditionalGuest> additionalGuests;
  private BookingPreference bookingPreference;
  private Business business;
  private Boolean businessUse;
  private String companyId;
  private String companyName;
  private ContactDetail contactDetail;
  private String guestHistoryNumber;
  private PaymentPreference paymentPreference;
  private String sessionId;

}
