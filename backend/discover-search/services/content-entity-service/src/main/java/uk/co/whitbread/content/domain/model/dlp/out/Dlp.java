package uk.co.whitbread.content.domain.model.dlp.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Dlp {

  private String title;
  private List<DlpItem> dlpItems;
}
