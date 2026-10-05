package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

  @NotNull
  private String username;
  @NotNull
  private String location;
}
