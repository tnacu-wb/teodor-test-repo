package uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationBaseClassDto;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class LabelsRequestDto extends LocalizationBaseClassDto {

  @NotNull
  @Parameter(in = ParameterIn.PATH, name = "category", example = "amend",
      required = true, schema = @Schema(type = "string"))
  CategoryEnumDto category;

  @Parameter(in = ParameterIn.QUERY, name = "labels", example = "label1,label2",
      schema = @Schema(type = "string"))
  List<String> labels;

  public LabelsRequestDto(String country, String language, CategoryEnumDto category, List<String> labels) {
    super(country, language);
    this.category = category;
    this.labels = labels;
  }

  protected LabelsRequestDto(final LabelsRequestDtoBuilder<?, ?> b) {
    super(b);
    this.category = b.category;
    this.labels = b.labels;
  }
}
