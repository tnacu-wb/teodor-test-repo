package uk.co.whitbread.hotel.register.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {

  AUTH0_CREATE_USER_ERROR_CODE("7002"),
  UL_UNAUTHORIZED_ERROR_CODE("7011");


  private final String code;


}
