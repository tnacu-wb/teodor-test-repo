package uk.co.whitbread.content.domain.model.inn.business.header.out;

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

  private List<String> months;
  private List<String> weekdaysShort;
  private String reset;
  private String done;
  private String checkOut;

}
