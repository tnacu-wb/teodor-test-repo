package uk.co.whitbread.content.domain.model.inn.business.header.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Form {

  private String where;
  private String whereIcon;
  private String calendarIcon;
  private String guestIcon;
  private String whereDismissIcon;
  private String search;
  private String hotelsLabel;
  private String searchIcon;
  private String searchEdit;
  private DatePicker datePicker;
  private String room;
  private String adultsHelperText;
  private String childrenHelperText;
  private String includeCot;
  private String cotLimit;
  private String roomType;
  private String removeRoom;
  private String invalidLocation;
  private String invalidNights;
  private String invalidRooms;

}
