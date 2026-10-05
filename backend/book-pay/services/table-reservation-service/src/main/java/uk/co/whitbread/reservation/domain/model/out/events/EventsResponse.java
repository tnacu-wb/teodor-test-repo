package uk.co.whitbread.reservation.domain.model.out.events;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventsResponse {

  private int adults;
  private String areaId;
  private String areaName;
  private String bookingReference;
  private String cancelLink;
  private int children;
  private Consent consent;
  private String date;
  private String editLink;
  private String emailAddress;
  private String firstname;
  private String id;
  private String lastname;
  private String name;
  private String occasionId;
  private String occasionName;
  private String siteId;
  private String siteName;
  private String specialRequest;
  private String telephoneNumber;
  private String siteTimezone;
  private String time;
  private int turnTimeMinutes;
  private List<Menus> menus;

  public EventsResponse(Consent consent) {
    this.consent = consent;
  }

}
