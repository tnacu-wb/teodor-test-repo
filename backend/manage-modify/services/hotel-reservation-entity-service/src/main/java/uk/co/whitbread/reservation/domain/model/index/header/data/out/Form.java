package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Form {

  private String childrenHelperText;
  private String invalidFutureDate;
  private String summary;
  private String invalidReference;
  private String bookingReference;
  private String adultsHelperText;
  private String whereEmailLandingPage;
  private String hotelsLabel;
  private String change;
  private String invalidNights;
  private String bookingSurnameLabel;
  private String changeGuestRoom;
  private String lowerPrices;
  private String invalidDate;
  private String search;
  private String update;
  private String bookingTypeInvalid;
  private String findBookingDescription;
  private String snowdropErrorHotelDirectory;
  private String cotLimit;
  private String groupBooking;
  private String roomType;
  private String invalidPastDate;
  private String bookingInvalid;
  private String where;
  private String bookingReferenceLabel;
  private String bookingSurname;
  private String invalidRooms;
  private String invalidLocation;
  private String snowdropErrorUrl;
  private String includeCot;
  private String snowdropErrorLinkConnector;
  private String checkinOnlineLandingPageTitle;
  private String removeRoom;
  private String snowdropErrorRetry;
  private String arrivalDateLabel;
  private String checkinOnlineLandingPageFormTitle;
  private String checkout;
  private Integer numberOfNights;
  private String findBookingTitle;
  private String snowdropError;
  private String checkinOnlineLandingPageDescription;
  private String invalidSurname;
  private DatePicker datePicker;
}
