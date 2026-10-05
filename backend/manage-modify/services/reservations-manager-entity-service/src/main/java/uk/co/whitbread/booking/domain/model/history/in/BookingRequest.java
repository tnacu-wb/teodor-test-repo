package uk.co.whitbread.booking.domain.model.history.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BookingRequest {

  private boolean business;
  private BookingStatus typeOfBooking;
  private SortOrder sortOrder;
  private FilterTypes filterType;
  private String filterValue;
  private boolean includeCheckInBookings;
  private String continuationToken;
  private Integer pageSize;
  private Integer pageIndex;
}
