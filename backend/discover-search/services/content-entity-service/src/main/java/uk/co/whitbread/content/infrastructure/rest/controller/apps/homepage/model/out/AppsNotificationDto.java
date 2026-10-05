package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppsNotificationDto {
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