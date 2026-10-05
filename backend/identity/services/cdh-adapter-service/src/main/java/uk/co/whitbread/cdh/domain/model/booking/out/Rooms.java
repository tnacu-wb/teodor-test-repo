package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rooms {

  @JsonProperty("ReservationId")
  private String reservationId;

  @JsonProperty("InvoiceNumber")
  private String invoiceNumber;

  @JsonProperty("Status")
  private String status;

  @JsonProperty("RoomId")
  private String roomId;

  @JsonProperty("Cot")
  private boolean cot;

  @JsonProperty("NoOfAdults")
  private String noOfAdults;

  @JsonProperty("NoOfChildren")
  private String noOfChildren;

  @JsonProperty("RoomType")
  private String roomType;

  @JsonProperty("RoomNumber")
  private String roomNumber;

  @JsonProperty("CarDetails")
  private boolean carDetails;

  @JsonProperty("PaymentCard")
  private List<PaymentCard> paymentCards;

  @JsonProperty("RoomCost")
  private Price roomCost;

  @JsonProperty("UpsellTotalCost")
  private Price upsellTotalCost;

  @JsonProperty("TotalCost")
  private Price totalCost;

  @JsonProperty("Guests")
  private List<Guests> guests;
}
