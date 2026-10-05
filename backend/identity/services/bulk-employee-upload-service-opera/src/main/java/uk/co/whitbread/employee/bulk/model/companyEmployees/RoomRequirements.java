package uk.co.whitbread.employee.bulk.model.companyEmployees;

import lombok.Data;

@Data
public class RoomRequirements {
  
  private Integer adults;
  private Integer children;
  private Boolean cotRequired;
  private String hotelBrand;
  private String lettingType;
  private String type;
}
