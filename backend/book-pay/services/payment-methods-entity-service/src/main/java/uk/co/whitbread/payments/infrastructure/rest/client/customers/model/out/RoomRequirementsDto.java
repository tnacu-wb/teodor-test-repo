package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import lombok.Data;

@Data
public class RoomRequirementsDto {
  private Long adults;
  private Long children;
  private Boolean cotRequired;
  private String hotelBrand;
  private String lettingType;
  private String type;
}
