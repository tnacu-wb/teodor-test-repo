package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendDistributionStayDatesRequest {
  @NotNull
  AmendStayDatesRequest amendStayDatesRequest;

  Integer adults;
  Integer children;
  String wbRoomType;
  String externalReference;
  Boolean isOta;
  Boolean cotRequired;
}
