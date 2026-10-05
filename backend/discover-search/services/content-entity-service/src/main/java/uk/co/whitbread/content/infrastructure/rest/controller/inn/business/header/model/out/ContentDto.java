package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.InnBResultsDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

  private HeaderDto header;
  private GlobalDto global;
  private List<CountryDto> countries;
  private FormDto form;
  private InnBResultsDto results;
  private AuthenticationDto authentication;
  private ContactBannerDto contactBanner;

}
