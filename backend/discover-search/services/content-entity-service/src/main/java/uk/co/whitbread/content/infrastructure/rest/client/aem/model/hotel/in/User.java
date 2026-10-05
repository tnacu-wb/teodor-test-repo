package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

  @NotNull
  private String username;
  @NotNull
  private String location;
}
