package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

  private GlobalDto global;
  private MenuDto menu;
  private List<CountryDto> countries;
  private HeaderDto header;
  private List<SubNavDto> subNav;
  private AuthenticationDto authentication;
  private SeoDto seo;
  private FaviconDto favicon;
  private HeroDto hero;
}
