package uk.co.whitbread.content.infrastructure.rest.controller.model.in;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class LocalizationDto extends LocalizationBaseClassDto {

  public LocalizationDto(String country, String language) {
    super(country, language);
  }

  protected LocalizationDto(final LocalizationDtoBuilder<?, ?> b) {
    super(b);
  }
}
