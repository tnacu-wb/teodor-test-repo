package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import lombok.Data;

@Data
public class LeadGuestDto {

  private String title;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String language;
  private BookerAddressDto address;

}
