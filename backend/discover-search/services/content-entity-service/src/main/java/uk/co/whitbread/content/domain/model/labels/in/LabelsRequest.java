package uk.co.whitbread.content.domain.model.labels.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class LabelsRequest implements SelfValidation<LabelsRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotNull
  private CategoryEnum category;
  private List<String> labels;

  public LabelsRequest(String country, String language, CategoryEnum category, List<String> labels) {
    this.country = country;
    this.language = language;
    this.category = category;
    this.labels = labels;
    this.validateSelf();
  }
}