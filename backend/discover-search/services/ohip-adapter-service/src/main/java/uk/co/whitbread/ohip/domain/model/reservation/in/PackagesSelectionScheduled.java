package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PackagesSelectionScheduled extends PackagesSelection {

  private List<String> scheduledDates;
}
