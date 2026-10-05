package uk.co.whitbread.shared.commons.logging.mask;

import com.fasterxml.jackson.core.JsonStreamContext;
import lombok.RequiredArgsConstructor;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.mask.ValueMasker;

import java.util.Optional;

@RequiredArgsConstructor
public class LogstashMasking implements ValueMasker {

  private final MaskingPattern maskingPattern;

  @Override
  public Object mask(JsonStreamContext context, Object value) {
    var contextIndex = Optional.ofNullable(context)
        .map(JsonStreamContext::getCurrentName)
        .orElse("");
    if (MessageJsonProvider.FIELD_MESSAGE.equalsIgnoreCase(contextIndex) && value instanceof CharSequence) {
      return maskingPattern.maskMessage((String) value);
    }
    return value;
  }
}