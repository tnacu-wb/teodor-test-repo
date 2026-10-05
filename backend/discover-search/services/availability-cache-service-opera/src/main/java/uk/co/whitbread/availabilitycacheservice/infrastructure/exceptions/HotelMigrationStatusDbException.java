package uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions;

public class HotelMigrationStatusDbException extends RuntimeException {

  public HotelMigrationStatusDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public HotelMigrationStatusDbException(String message) {
    super(message);
  }
}
