package uk.co.whitbread.hotel.account.service.worldline.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactDetailsError {
  REQUIRED_TITLE(1, "Title is required"),
  INVALID_TITLE(2, "Title is invalid"),
  REQUIRED_FORENAME(3, "ForeName is required"),
  INVALID_FORENAME(4, "ForeName cannot have special and numeric characters"),
  REQUIRED_LASTNAME(5, "LastName is required"),
  INVALID_LASTNAME(6, "LastName cannot have special and numeric characters"),
  REQUIRED_EMAIL(7, "Email is required"),
  INVALID_EMAIL(8, "Invalid email format"),
  INVALID_MOBILE_NUMBER(9, "Invalid mobile number"),
  INVALID_TELEPHONE_NUMBER(10, "Invalid telephone number: Please enter a valid number"),
  INVALID_LANDLINE_MOBILE_NUMBER(11, "Please enter only numbers between 0-9");

  private final int code;
  private final String message;

}
