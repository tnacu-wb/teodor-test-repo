package uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoBoxResponseDto {

  private String title;
  private String button;
  private String whenInvalid;
  private String whenMultipleRedeem;
  private String whenSuccess;
  private String whenEmpty;
  private String whenCodeExpired;
  private String whenUnavailable;
  private String whenCodeAlreadyApplied;
  private String whenMaxRoomsExceeded;
  private String whenMinRoomsNotMet;
}