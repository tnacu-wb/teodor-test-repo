package uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResultsDto {

  private String bookingReference;

  private String hotelArea;

  private String hotelName;

  private String hotelPostcode;

  private String hotelPhoneNumber;

  private String arrivalDate;

  private String departureDate;

  private String noOfAdults;

  private String noOfChildren;

  private List<GuestsDto> guests;

  private EmergencyReportBookerDto booker;

  private String status;

}
