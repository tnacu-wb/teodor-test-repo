package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class RoomRequirements {
  private Long adults;
  private Long children;
  private Boolean cotRequired;
  private String hotelBrand;
  private String lettingType;
  private String type;
}
