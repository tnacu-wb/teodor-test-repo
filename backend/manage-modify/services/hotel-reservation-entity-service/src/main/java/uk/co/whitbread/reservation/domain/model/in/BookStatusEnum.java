package uk.co.whitbread.reservation.domain.model.in;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum BookStatusEnum {

  CHECKEDIN("Checked-In", 1),
  UPCOMING("Upcoming", 2),
  PAST("Past", 3),
  UNDEFINED("Undefined", 4),
  CANCELLED("Cancelled", 5);

  private final String status;
  private final int statusSortOrder;

  BookStatusEnum(String status, int statusSortOrder) {
    this.status = status;
    this.statusSortOrder = statusSortOrder;
  }

  private static final Map<Integer, String> BOOKING_STATS_MAP = new HashMap<>();

  static {
    for (BookStatusEnum bookStatusEnum : BookStatusEnum.values()) {
      BOOKING_STATS_MAP.put(bookStatusEnum.statusSortOrder, bookStatusEnum.status);
    }
  }

  public static Set<Integer> getBookingStatusOrder() {
    return BOOKING_STATS_MAP.keySet();
  }

  public static String getBookingStatus(Integer statusOrder) {
    return BOOKING_STATS_MAP.get(statusOrder);
  }

}
