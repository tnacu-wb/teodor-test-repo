package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomReservationPackagesScheduledRequest {

  @NotEmpty
  private String reservationsId;

  @Valid
  private List<PackagesSelectionScheduled> addPackages;

  @Valid
  private List<PackagesSelection> removePackages;

}
