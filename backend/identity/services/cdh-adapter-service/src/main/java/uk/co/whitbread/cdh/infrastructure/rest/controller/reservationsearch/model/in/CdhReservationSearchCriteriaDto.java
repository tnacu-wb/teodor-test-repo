package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhReservationSearchCriteriaDto {

  private String bookingReference;
  private String lastName;
  private String emailAddress;
  private String hotelCode;
  private String hotelName;
  private String postalCode;
  private String telephone;
  private String companyName;
  private String purchaseOrder;
  private String customerReference;
  private String customerAccountId;
  private String thirdPartyReference;
  private String status;
  private Integer pageSize;
  private String continuationToken;
  private Integer pageNumber;
  private String bartGuestHistoryNumber;
  private String employeeAccountId;
  private String companyAccountId;
  private Boolean bookingsDatabaseSearch;
  private Integer reservationId;
  private String bookingDate;
  private String cancellationDate;
  private String arrivalDateFrom;
  private String arrivalDateTo;
  private Integer upcomingDays;
  private Integer pastDays;
  private Integer cancelledDays;
}
