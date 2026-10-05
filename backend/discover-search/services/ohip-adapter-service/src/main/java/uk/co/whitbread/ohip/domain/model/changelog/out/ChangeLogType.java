package uk.co.whitbread.ohip.domain.model.changelog.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogType {

  String logDate;
  String logUserName;
  String actionType;
  String actionDescription;
}
