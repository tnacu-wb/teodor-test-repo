package uk.co.whitbread.piba.registration.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TetheredUserRequest {
  private Scheme scheme;
  private String companyId;
  private List<TetheredGuidDetails> tetheredGuids;
}

