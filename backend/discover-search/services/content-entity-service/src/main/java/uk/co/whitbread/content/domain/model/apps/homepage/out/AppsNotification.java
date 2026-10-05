package uk.co.whitbread.content.domain.model.apps.homepage.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class AppsNotification extends AppsHomepageFieldsCard {
  @NotEmpty
  private String type;
  private String title;
  @NotEmpty
  private String message;
  private String linkLabel;
  private String linkPath;
  private Boolean openLinkInApp;
  private Boolean dismissible;
}