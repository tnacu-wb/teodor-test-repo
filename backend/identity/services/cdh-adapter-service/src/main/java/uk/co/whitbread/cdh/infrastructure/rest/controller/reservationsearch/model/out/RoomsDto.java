package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomsDto {

  private String reservationId;
  private String invoiceNumber;
  private String status;
  private String roomId;
  private boolean cot;
  private String noOfAdults;
  private String noOfChildren;
  private String roomType;
  private String roomNumber;
  private boolean carDetails;
  private List<PaymentCardDto> paymentCards;
  private PriceDto roomCost;
  private PriceDto upsellTotalCost;
  private PriceDto totalCost;
  private List<GuestsDto> guests;
}
