package uk.co.whitbread.kiosk.domain.model.checkin.out;

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
  private String profileType;


}
