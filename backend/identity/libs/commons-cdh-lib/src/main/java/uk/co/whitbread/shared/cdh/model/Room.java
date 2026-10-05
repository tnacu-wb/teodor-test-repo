package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {

  @JsonProperty("InvoiceNumber")
  private String invoiceNumber;

  @JsonProperty("Status")
  private String bookingStatus;

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

  @JsonProperty("Guests")
  private List<Guest> guests;

  @JsonProperty("RoomCost")
  private Price roomCost;

  @JsonProperty("UpsellItems")
  private List<UpsellItem> upsellItems;

  @JsonProperty("PaymentCard")
  private List<BusinessPaymentCard> paymentCard;
}
