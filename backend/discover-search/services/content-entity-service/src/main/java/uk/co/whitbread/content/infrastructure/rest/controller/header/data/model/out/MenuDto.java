package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {

  private String mobileMenuButton;
  private String language;
  private String business;
  private String languageButton;
  private String tick;
  private String logIn;
  @JsonProperty("discoverPI")
  private String discoverPi;
  private String findBooking;
  private String bookHotel;
  private String guestAccount;
  private String changeLogs;
  private String agentMemo;
  private String promoCode;
}
