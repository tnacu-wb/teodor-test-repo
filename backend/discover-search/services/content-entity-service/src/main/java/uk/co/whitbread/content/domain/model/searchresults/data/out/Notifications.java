package uk.co.whitbread.content.domain.model.searchresults.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notifications {

  private String fullyBooked;
  private String openingSoon;
  private String noFilteredHotels;
  private String availabilitiesErrorMessage;
  private String errorTitle;
}