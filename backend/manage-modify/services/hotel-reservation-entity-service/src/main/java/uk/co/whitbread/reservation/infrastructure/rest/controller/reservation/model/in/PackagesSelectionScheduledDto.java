package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackagesSelectionScheduledDto {

  private String id;
  private Integer noOfSelections;
  private List<LocalDate> scheduledDates;
}
