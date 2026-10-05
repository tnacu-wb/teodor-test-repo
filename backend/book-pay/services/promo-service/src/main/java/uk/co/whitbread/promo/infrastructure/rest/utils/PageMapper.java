package uk.co.whitbread.promo.infrastructure.rest.utils;

import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PagedResponse;

@UtilityClass
public class PageMapper {

  public static <S, T> Page<T> map(Page<S> source, Function<S, T> mapper) {
    List<T> mapped = source.stream().map(mapper).toList();
    return new PageImpl<>(mapped, source.getPageable(), source.getTotalElements());
  }

  public static <T> PagedResponse<T> toPagedResponse(Page<T> page) {
    List<T> items = page.getContent();
    int pageNumber = page.getNumber();
    int pageSize = page.getSize();
    long totalElements = page.getTotalElements();
    int totalPages = page.getTotalPages();
    boolean hasNext = page.hasNext();

    long startIndex = totalElements == 0 ? 0 : (long) pageNumber * pageSize + 1;
    long endIndex =
        totalElements == 0 ? 0 : Math.min((long) (pageNumber + 1) * pageSize, totalElements);

    return new PagedResponse<>(
        items,
        pageNumber,
        pageSize,
        totalElements,
        totalPages,
        hasNext,
        startIndex,
        endIndex
    );
  }
}
