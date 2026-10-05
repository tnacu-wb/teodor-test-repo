package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.DlpItemDto;

@Data
@Builder
public class DlpDto {

  private String title;
  private List<DlpItemDto> dlpItems;
}
