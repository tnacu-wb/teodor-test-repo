package uk.co.whitbread.shared.commons.logging.mask;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsoleMasking extends PatternLayout {

  private final MaskingPattern maskingPattern;

  @Override
  public String doLayout(ILoggingEvent event) {
    return maskingPattern.maskMessage(super.doLayout(event));
  }

}