package uk.co.whitbread.booking.domain.model.information.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThumbnailImage {

  private String imageSrc;
  private List<String> tags;
}
