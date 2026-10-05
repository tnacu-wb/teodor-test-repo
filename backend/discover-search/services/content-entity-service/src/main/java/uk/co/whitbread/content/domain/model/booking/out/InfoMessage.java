package uk.co.whitbread.content.domain.model.booking.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoMessage {

  private String rate;
  private String rateCategory;
  private String rateDisplaySet;
  private List<Message> messages;
}
