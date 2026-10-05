package uk.co.whitbread.ohip.domain.model.changelog.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogResponse {

  private ChangeLogListType activityLog;
}
