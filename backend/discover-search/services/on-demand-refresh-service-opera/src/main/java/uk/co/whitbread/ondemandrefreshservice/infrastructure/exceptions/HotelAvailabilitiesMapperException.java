package uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions;

public class HotelAvailabilitiesMapperException extends RuntimeException{
  public HotelAvailabilitiesMapperException(String message, Throwable cause) {
    super(message, cause);
  }

  public HotelAvailabilitiesMapperException(String message) {
    super(message);
  }
}

