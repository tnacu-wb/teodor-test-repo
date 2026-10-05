package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekdayValues {

  private String weekBfEndTime;
  private String weekDinnerEndTime;
  private String weekBfStartTime;
  private String weekDinnerStartTime;

}