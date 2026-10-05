package uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FooterResponseDto {

  private List<LinkTabsDto> tabs;
  private List<SocialLinksDto> socialMediaIcons;
  private String copyrightInfo;
  private NewsletterSignupDto newsletterSignup;
  private List<LinkItemsDto> bottomLinks;

}
