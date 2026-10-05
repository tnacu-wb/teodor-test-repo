package uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdfsRequestDto {
  @NotEmpty
  private Set<String> reservationIds;
  @NotNull
  private String hotelId;
  @NotEmpty
  private List<CharacterUdfDto> udfs;
}
