package uk.co.whitbread.kiosk.domain.model.roomallocation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationResponse {

  private List<Links> links;
  private String roomId;

}
