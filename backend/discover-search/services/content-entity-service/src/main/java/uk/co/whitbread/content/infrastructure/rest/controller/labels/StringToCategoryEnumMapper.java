package uk.co.whitbread.content.infrastructure.rest.controller.labels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.domain.model.ErrorCode;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.exceptions.LabelsBadRequestException;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.CategoryEnumDto;

@Slf4j
@Component
public class StringToCategoryEnumMapper implements Converter<String, CategoryEnumDto> {

  @Override
  public CategoryEnumDto convert(String source) {
    try {
      return CategoryEnumDto
          .valueOf(source.trim().replace('-', '_').toUpperCase());
    } catch (IllegalArgumentException e) {
      var exception = new LabelsBadRequestException(
          ErrorCode.DIGITAL_INVALID_CATEGORY_TO_STRING_EXCEPTION,
          String.format("Failed to convert! Invalid Category: %s", source), e);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}
