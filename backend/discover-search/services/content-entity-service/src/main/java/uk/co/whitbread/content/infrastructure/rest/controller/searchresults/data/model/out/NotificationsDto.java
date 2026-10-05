package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsDto {

  private String fullyBooked;
  private String openingSoon;
  private String noFilteredHotels;
  private String availabilitiesErrorMessage;
  private String errorTitle;

}
