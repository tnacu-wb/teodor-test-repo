package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationParticipant {

  private boolean initiator;
  private Integer participantId;
  private boolean delegated;
  private boolean terms;
  private boolean directDebit;
  private String email;
  private String shared;
  private String name;

}
