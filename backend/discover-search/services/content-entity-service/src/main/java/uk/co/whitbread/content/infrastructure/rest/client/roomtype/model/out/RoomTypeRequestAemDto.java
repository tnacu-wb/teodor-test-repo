package uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeRequestAemDto {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String brand;

  private String hotelId;


}
