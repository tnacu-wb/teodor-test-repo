package uk.co.whitbread.content.domain.model.apps.homepage.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AppsHomepageLogo {

  private String imagePath;
}