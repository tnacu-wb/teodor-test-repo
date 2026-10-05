package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItemsRequestDto {

  @Valid
  private List<String> reservationIds;
  private String hotelId;
  private String companyId;
  private BusinessItemsDto businessItems;
  private String channel;
}
