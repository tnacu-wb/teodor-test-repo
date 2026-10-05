package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDetailsDto {

  @NotNull
  private String title;
  @NotNull
  private String firstName;
  @NotNull
  private String lastName;
}
