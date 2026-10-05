package uk.co.whitbread.reservation.domain.model.out.enquiry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.out.events.Consent;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventOrEquiryRequest {

  private int children;
  private String specialRequest;
  private String siteId;
  private String occasionId;
  private String date;
  private String time;
  private int adults;
  private String firstname;
  private String lastname;
  private String emailAddress;

  @JsonIgnore
  private int turnTimeMinutes;
  private String telephoneNumber;
  private Consent consent;
  private List<String> menuIds;

}
