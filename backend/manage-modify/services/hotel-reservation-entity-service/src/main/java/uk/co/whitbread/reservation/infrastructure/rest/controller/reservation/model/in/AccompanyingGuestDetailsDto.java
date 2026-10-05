package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.Data;

@Data
public class AccompanyingGuestDetailsDto {

  private String title;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String employeeAccountId;
  private StayingGuestAdditionalDetailsDto additionalDetails;
}
