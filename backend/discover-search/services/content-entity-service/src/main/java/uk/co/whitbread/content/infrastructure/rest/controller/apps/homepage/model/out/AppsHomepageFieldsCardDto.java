package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AppsHomepageFieldsCardDto {

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