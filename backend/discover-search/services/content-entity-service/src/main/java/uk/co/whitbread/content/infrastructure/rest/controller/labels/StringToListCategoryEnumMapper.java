package uk.co.whitbread.content.infrastructure.rest.controller.labels;

import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_INVALID_CATEGORY_EXCEPTION;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.exceptions.LabelsBadRequestException;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.CategoryEnumDto;

@Component
@Slf4j
public class StringToListCategoryEnumMapper implements Converter<String, List<CategoryEnumDto>> {

  @Override
  public List<CategoryEnumDto> convert(String source) {
    try {
      return Arrays.asList(source.split(",")).stream().map(s -> CategoryEnumDto
          .valueOf(s.trim().replace('-', '_').toUpperCase())).toList();

    } catch (IllegalArgumentException e) {
      var exception = new LabelsBadRequestException(DIGITAL_INVALID_CATEGORY_EXCEPTION,
          String.format("Failed to convert to known category!. Invalid Category %s", source), e);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}
