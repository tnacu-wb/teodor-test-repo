package uk.co.whitbread.review.infrastructure.rest.client.review.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwardDto {
  private String awardType;
  private Integer year;
  private AwardImagesDto images;
  private String image;
}
