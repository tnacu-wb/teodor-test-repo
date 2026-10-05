package uk.co.whitbread.promo.domain.model.promobatch.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@AllArgsConstructor
public class PromoBatchSummaryPage {

  private List<PromoBatchSummary> promoBatchSummary;
  private long totalElements;
  private int totalPages;
  private int pageNumber;
  private int pageSize;
  private Pageable pageable;
}