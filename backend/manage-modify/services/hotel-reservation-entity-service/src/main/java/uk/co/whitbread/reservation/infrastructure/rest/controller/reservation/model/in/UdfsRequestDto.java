package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.in.CiolStatusEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdfsRequestDto {
  @NotEmpty
  private Set<String> reservationIds;
  @NotNull
  private String hotelId;
  @NotNull
  private CiolStatusEnum ciolStatus;
}