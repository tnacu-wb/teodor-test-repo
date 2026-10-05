package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

  private String mobileMenuButton;
  private String language;
  private String business;
  private String languageButton;
  private String tick;
  private String logIn;
  private String discoverPi;
  private String findBooking;
  private String bookHotel;
  private String guestAccount;
  private String changeLogs;
  private String agentMemo;
}
