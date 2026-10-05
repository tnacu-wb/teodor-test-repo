package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManageViewDto {

  private String title;
  private String description;
  private String saveSettingsButtonText;
  private String alwaysActiveText;
  private List<CookieGroupDto> cookieGroup;

}