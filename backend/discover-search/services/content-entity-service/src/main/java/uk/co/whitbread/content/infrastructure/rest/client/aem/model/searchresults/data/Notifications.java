package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

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