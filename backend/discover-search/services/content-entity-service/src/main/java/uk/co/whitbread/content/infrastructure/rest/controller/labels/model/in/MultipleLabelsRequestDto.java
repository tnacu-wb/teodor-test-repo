package uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationBaseClassDto;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class MultipleLabelsRequestDto extends LocalizationBaseClassDto {

  @NotNull
  @Size(min = 1, max = 100)
  @Parameter(in = ParameterIn.QUERY, name = "categories", required = true)
  List<CategoryEnumDto> categories;

  public MultipleLabelsRequestDto(String country, String language,
      List<CategoryEnumDto> categories) {
    super(country, language);
    this.categories = categories;
  }

  protected MultipleLabelsRequestDto(final MultipleLabelsRequestDtoBuilder<?, ?> b) {
    super(b);
    this.categories = b.categories;
  }

}
