package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in;

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
public class PageDataRequestDto extends LocalizationBaseClassDto {

  @NotNull
  @Size(min = 1, max = 100)
  @Parameter(in = ParameterIn.QUERY, name = "dictionaries", required = true)
  List<DictionaryEnumDto> dictionaries;

  public PageDataRequestDto(String country, String language,
      List<DictionaryEnumDto> dictionaries) {
    super(country, language);
    this.dictionaries = dictionaries;
  }

  protected PageDataRequestDto(final PageDataRequestDtoBuilder<?, ?> b) {
    super(b);
    this.dictionaries = b.dictionaries;
  }
}
