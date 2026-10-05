package uk.co.whitbread.cdh.domain.model.report.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.cdh.domain.model.booking.out.Guests;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResults {

  @JsonProperty("BookingReference")
  private String bookingReference;

  @JsonProperty("HotelArea")
  private String hotelArea;

  @JsonProperty("HotelName")
  private String hotelName;

  @JsonProperty("HotelPostcode")
  private String hotelPostcode;

  @JsonProperty("HotelPhoneNumber")
  private String hotelPhoneNumber;

  @JsonProperty("ArrivalDate")
  private String arrivalDate;

  @JsonProperty("DepartureDate")
  private String departureDate;

  @JsonProperty("NoOfAdults")
  private String noOfAdults;

  @JsonProperty("NoOfChildren")
  private String noOfChildren;

  @JsonProperty("Guests")
  private List<Guests> guests;

  @JsonProperty("Booker")
  private EmergencyReportBooker booker;

  @JsonProperty("Status")
  private String status;

}
