package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RequestValidator {

  public static String isValidName(String name) {

    String validate = "";
    if (name == null || name.isEmpty()) {
      validate = "Name cannot be blank or null.";
    } else if (name.length() < 2 || name.length() > 100) {
      validate = "Name length should be between 2 and 100 characters.";
    } else if (!name.matches("^[a-zA-Z ]+$")) {
      validate = "Name can only contain alphabetic characters and spaces.";
    } else if (name.matches(".*\\d.*")) {
      validate = "Name cannot contain numeric values.";
    }
    return validate;
  }


  public static String isValidPhoneNumber(String telephoneNumber) {
    String validate = "";
    if (telephoneNumber == null || telephoneNumber.isEmpty()) {
      validate = "Phone number cannot be blank or null.";
    } else if (telephoneNumber.length() < 11 || telephoneNumber.length() > 15) {
      validate = "Phone number length should be between 11 to 15 digits long.";
    } else if (!telephoneNumber.matches("^[+0]\\d+$")) {
      validate = "Phone number can only contain numeric digits (0-9) and starts with 0 or +.";
    }
    return validate;
  }

  public static String isValidEmailAddress(String emailAddress) {
    String validate = "";
    if (emailAddress == null || emailAddress.isEmpty()) {
      validate = "Email Address cannot be blank or null.";
    } else if (!emailAddress.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")) {
      validate = "Email Address is not in a format";
    }
    return validate;
  }

  public static String isValidInteger(int num) {
    String validate = "";
    if (num < 0) {
      validate = "Field should be a non-negative integer.";
    }
    return validate;
  }

}
