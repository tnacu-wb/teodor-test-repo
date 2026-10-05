package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatePicker {

  private String pickaDate;
  private String unknownDates;
  private List<String> months;
  private String invalidDate;
  private List<String> weekdaysShort;
  private List<String> weekdays;
  private String previousMonth;
  private String reset;
  private String nextMonth;
  private String checkOut;
}
