package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlpDto {

  private String title;
  private List<DlpItemDto> dlpItems;
}
