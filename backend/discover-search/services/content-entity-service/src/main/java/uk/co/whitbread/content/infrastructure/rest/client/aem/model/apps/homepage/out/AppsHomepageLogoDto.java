package uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppsHomepageLogoDto {

  private String imagePath;
}