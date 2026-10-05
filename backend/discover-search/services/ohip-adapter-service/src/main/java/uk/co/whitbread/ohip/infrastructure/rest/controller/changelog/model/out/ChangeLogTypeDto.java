package uk.co.whitbread.ohip.infrastructure.rest.controller.changelog.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogTypeDto {

  String logDate;
  String logUserName;
  String actionType;
  String actionDescription;
}
