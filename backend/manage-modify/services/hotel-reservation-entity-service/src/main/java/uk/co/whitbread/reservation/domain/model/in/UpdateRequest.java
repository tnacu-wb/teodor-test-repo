package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {

  private String basketReferenceId;
  private String hotelId;
  private List<String> reservationIds;
  private String rateCode;
  private String startDate;
  private String endDate;
  private List<String> roomTypes;
  private String currency;
  private List<Integer> adultsNumber;
  private List<Integer> childrenNumber;
}
