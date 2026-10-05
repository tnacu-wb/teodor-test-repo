package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitializeApplicationResponse {

  private String applicationGUID;
  private String applicationId;

}
