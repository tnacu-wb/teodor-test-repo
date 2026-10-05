package uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogTypeDto {

  String date;
  String time;
  String actionType;
  String actionDescription;
  String user;
}
