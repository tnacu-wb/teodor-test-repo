package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDto {

  private String childrenHelperText;
  private String adultsHelperText;
  private String cotLimit;
  private String includeCot;
  private String removeRoom;
  private String checkout;
  private String roomType;
  private String where;
  private String invalidLocation;
  private String invalidPastDate;
  private String invalidDate;
  private String invalidNights;
  private String invalidRooms;
  private String snowdropError;
  private String snowdropErrorRetry;
  private String invalidFutureDate;
  private String findBookingTitle;
  private String findBookingDescription;
  private String bookingReferenceLabel;
  private String bookingSurnameLabel;
  private String arrivalDateLabel;
  private String invalidReference;
  private String invalidSurname;
  private String searchBookingError;
  private String bookingInvalid;
}
