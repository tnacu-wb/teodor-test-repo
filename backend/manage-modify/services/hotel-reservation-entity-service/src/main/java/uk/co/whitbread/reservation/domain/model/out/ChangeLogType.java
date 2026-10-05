package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogType {

  String date;
  String time;
  String actionType;
  String actionDescription;
  String user;
  String logUserName;
  String logDate;
}
