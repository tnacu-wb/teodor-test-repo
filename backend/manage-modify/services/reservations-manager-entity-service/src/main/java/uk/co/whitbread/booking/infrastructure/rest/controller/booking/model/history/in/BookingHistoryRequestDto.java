package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingHistoryRequestDto {

  private boolean business;
  private BookingStatusRequestDto typeOfBooking;
  private SortOrderRequestDto sortOrder;
  private FilterTypeDto filterType;
  private String filterValue;
  private boolean includeCheckInBookings;
  private String continuationToken;
  private Integer pageSize;
  private Integer pageIndex;
  private String channel;
  private String subchannel;
}
