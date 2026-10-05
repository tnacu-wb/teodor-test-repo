package uk.co.whitbread.booking.infrastructure.rest.client.content.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentsDto {
  private String path;
  private String label;
  private String type;
}
