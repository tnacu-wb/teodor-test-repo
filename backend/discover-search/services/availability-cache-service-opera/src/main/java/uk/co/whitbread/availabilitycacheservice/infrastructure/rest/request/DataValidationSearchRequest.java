package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.DateFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataValidationSearchRequest {

  @NotNull
  private Set<String> hotelCodes;

  @NotBlank
  @DateFormat
  private String availabilityFromDate;

  @NotBlank
  @DateFormat
  private String availabilityUntilDate;

  private Set<String> roomTypes;

  private Set<String> rateCodes;

}
