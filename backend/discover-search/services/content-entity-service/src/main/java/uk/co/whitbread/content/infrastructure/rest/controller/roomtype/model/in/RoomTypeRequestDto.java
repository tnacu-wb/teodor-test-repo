package uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomTypeRequestDto {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String brand;
  private String hotelId;

  public RoomTypeRequestDto(String country, String language, String brand, String hotelId) {
    this.country = country;
    this.language = language;
    this.brand = brand;
    this.hotelId = hotelId;
  }
}
