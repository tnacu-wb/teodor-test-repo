package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequestDto {

  @NotNull
  private String roomType;
  @NotNull
  private Integer adultsNumber;
  private Integer childrenNumber;
  private Boolean cotRequired;

}
