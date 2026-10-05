package uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class HotelAvailabilitiesExceptionResponse {

  private LocalDateTime timestamp;
  private HttpStatus status;
  private List<String> errors;

  public HotelAvailabilitiesExceptionResponse(final HttpStatus status, final List<String> errors) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.errors = errors;
  }

  public HotelAvailabilitiesExceptionResponse(final HttpStatus status, final String error) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.errors = Arrays.asList(error);
  }

}
