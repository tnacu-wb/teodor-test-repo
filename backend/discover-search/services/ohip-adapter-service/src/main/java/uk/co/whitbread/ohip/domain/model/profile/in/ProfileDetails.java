package uk.co.whitbread.ohip.domain.model.profile.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDetails {

  private Customer customer;
  private ProfileAddresses addresses;
  private Telephones telephones;
  private ProfileEmails emails;
  private MailingActions mailingActions;
  private PrivacyInfo privacyInfo;
  private String requestForHotel;
  private boolean markAsRecentlyAccessed;
  private String profileType;


}
