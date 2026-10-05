package uk.co.whitbread.content.domain.model.inn.business.pagedata.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class PageDataRequest implements SelfValidation<PageDataRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotNull
  @Size(min = 1, max = 100)
  private List<DictionaryEnum> dictionaries;

  public PageDataRequest(String country, String language, List<DictionaryEnum> dictionaries) {
    this.country = country;
    this.language = language;
    this.dictionaries = dictionaries;
    this.validateSelf();
  }

}
