package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldlineAppCancelRequestDto {
  private String reasonDescription;
}
