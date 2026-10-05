package uk.co.whitbread.ohip.domain.model.changelog.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogListType {

  @Singular("activityLogType")
  List<ChangeLogType> activityLog;
  private Integer totalPages;
  private Integer offset;
  private Integer limit;
  private Boolean hasMore;
  private Integer totalResults;
  private Integer count;
}
