package uk.co.whitbread.content.domain.model.index.header.data.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCodes {

  @JsonProperty("double")
  private String doubleValue;
  private String family;
  private String accessible;
  private String single;
  private String twin;
}
