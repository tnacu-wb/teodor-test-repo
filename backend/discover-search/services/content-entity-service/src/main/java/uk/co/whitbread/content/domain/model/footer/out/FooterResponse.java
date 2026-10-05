package uk.co.whitbread.content.domain.model.footer.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FooterResponse {

  private List<LinkTabs> tabs;
  private List<SocialLinks> socialMediaIcons;
  private String copyrightInfo;
  private NewsletterSignup newsletterSignup;
  private List<LinkItems> bottomLinks;

}
