package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StayRequestDto implements Serializable {

  private boolean business;
  private String companyId;
  private String employeeId;
  private BookingStatusStayDto typeOfBooking;
  private SortOrderStayDto sortOrder;
  private FilterTypeStaysDto filterType;
  private String filterValue;
  private boolean includeCheckInBookings;
  private String continuationToken;
  private Integer pageSize;
  private Integer pageIndex;
}
