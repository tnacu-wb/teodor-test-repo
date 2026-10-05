package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppsHomepageLogoDto {

  private String imagePath;
}