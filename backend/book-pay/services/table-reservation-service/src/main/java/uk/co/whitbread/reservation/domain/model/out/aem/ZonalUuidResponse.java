package uk.co.whitbread.reservation.domain.model.out.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZonalUuidResponse {
  private String id;
  private String title;
  private String path;
  private String latitude;
  private String longitude;
  private String address1;
  private String address2;
  private String address3;
  private String address4;
  private String contactInfo;
  private String googleMapURL;
  private String externalSystemIdentifier;
  private String externalSourceSystem;
  private String bookingHeroImage;
  private String bookingHeroBackgroundImage;
}
