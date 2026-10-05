package uk.co.whitbread.booking.domain.model.history.out;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonalDetails {
  @NotNull
  private String title;
  @NotNull
  private String firstName;
  @NotNull
  private String lastName;
}
