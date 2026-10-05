package uk.co.whitbread.content.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInformationExtended extends HotelInformation {

  private String currencyCode;
  private String languageCode;
  private String timeZone;
  private String checkOutTime;
  private String checkInTime;
}