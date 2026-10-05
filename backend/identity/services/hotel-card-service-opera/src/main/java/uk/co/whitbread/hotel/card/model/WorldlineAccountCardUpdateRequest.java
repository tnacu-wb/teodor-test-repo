package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorldlineAccountCardUpdateRequest {

  private String displayName;
  private Integer cardLimit;
  private boolean restrictCardUsage;
  private String restrictionStart;
  private String restrictionEnd;
  private String apiUserGuid;

}
