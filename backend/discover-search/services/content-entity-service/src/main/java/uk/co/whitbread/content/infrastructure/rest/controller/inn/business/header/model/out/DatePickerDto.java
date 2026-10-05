package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatePickerDto {

  private List<String> months;
  private List<String> weekdaysShort;
  private String reset;
  private String done;
  private String checkOut;

}
