package uk.co.whitbread.content.domain.model.apps.homepage.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AppsHomepageFieldsCard {

  @NotEmpty
  private String imagePath;
  private String imageTag;
  private String subtitle;
  private String linkPath;
  private Boolean openLinkInApp;
  @NotEmpty
  private Integer order;
  private String trackingId;
}