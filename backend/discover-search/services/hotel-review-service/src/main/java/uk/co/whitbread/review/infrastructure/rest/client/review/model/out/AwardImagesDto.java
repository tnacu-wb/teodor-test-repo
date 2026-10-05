package uk.co.whitbread.review.infrastructure.rest.client.review.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwardImagesDto {
  private String small;
  private String large;
}
