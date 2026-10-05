package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experiments {

  private String emailLandingPagesCode;
  private String emailLandingPageContent;
  private String emailLandingPageCampaign;
  private String snowDropSortByKey;
  private Boolean snowDropRandom;
  private Boolean snowDropSortBy;
  private Boolean flexOnlyRate;
  private Boolean betaBusiness;
  private Dateless dateless;
}
