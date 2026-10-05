package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventsResponseDto {

  private int adults;
  private String areaId;
  private String areaName;
  private String bookingReference;
  private String braintreeCustomerId;
  private String cancelLink;
  private int children;
  private ConsentDto consent;
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
  private List<MenusDto> menus;
}
