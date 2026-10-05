package uk.co.whitbread.content.domain.model.labels.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class MultipleLabelsRequest implements SelfValidation<MultipleLabelsRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotNull
  @Size(min = 1, max = 100)
  private List<CategoryEnum> categories;

  public MultipleLabelsRequest(String country, String language, List<CategoryEnum> categories) {
    this.country = country;
    this.language = language;
    this.categories = categories;
    this.validateSelf();
  }
}
