package uk.co.whitbread.content.domain.model.apps.homepage.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AppsHomepageRequest {

  @NotEmpty
  private final String country;
  @NotEmpty
  private final String language;
  @NotEmpty
  private final String channel;
  @NotEmpty
  private final String subchannel;
}