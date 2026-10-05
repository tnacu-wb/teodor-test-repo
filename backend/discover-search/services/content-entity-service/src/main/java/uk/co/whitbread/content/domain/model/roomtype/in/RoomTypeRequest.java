package uk.co.whitbread.content.domain.model.roomtype.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class RoomTypeRequest implements SelfValidation<RoomTypeRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotEmpty
  private String brand;
  private String hotelId;

  public RoomTypeRequest(String country, String language, String brand, String hotelId) {
    this.country = country;
    this.language = language;
    this.brand = brand;
    this.hotelId = hotelId;
    this.validateSelf();
  }
}
