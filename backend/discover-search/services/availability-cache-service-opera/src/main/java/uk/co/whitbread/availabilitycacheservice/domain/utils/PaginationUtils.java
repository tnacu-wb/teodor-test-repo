package uk.co.whitbread.availabilitycacheservice.domain.utils;

import java.util.List;
import java.util.stream.IntStream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class PaginationUtils {

  public static <T> List<T> applyPagination(List<T> wholeList, int initialPageSize, int lazyLoadPageSize, int page) {
    int startsFrom = getStartingIndex(initialPageSize, lazyLoadPageSize, page);
    int endsTo = getEndingIndex(wholeList, initialPageSize, lazyLoadPageSize, page);

    return IntStream.range(startsFrom, endsTo)
        .filter(i -> i < wholeList.size() && wholeList.get(i) != null)
        .mapToObj(wholeList::get)
        .toList();
  }

  private static Integer getStartingIndex(int initialPageSize, int lazyLoadPageSize, int page) {
    int index = initialPageSize + (page - 2) * lazyLoadPageSize;
    return page == 1 ? 0 : index;
  }

  private static <T> Integer getEndingIndex(List<T> wholeList, int initialPageSize, int lazyLoadPageSize, int page) {
    return Math.min(wholeList.size(), (initialPageSize + (page - 1) * lazyLoadPageSize));
  }

}
