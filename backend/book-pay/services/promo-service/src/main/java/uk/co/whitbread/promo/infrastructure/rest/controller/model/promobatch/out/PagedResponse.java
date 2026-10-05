package uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {

  private List<T> items;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean hasNext;
  private long startIndex;
  private long endIndex;
}
