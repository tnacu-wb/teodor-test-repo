package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorldlineAccountCardInviteRequest {

  private Scheme scheme;
  private String registrationInfoTitle;
  private String registrationInfoForename;
  private String registrationInfoSurname;
  private String registrationInfoEmailAddress;
  private boolean sendMeCopyOfInvite;

}
