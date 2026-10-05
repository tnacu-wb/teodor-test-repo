package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomSubstitutionDto {

  private String type;
  private Boolean silent;
  private String specialRequest;
  private String accessibleSpecialRequest;
  private String codePackage;

}