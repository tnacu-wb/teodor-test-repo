package uk.co.whitbread.shared.commons.logging.logger;

import static ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;
import uk.co.whitbread.shared.commons.logging.mask.ConsoleMasking;
import uk.co.whitbread.shared.commons.logging.mask.MaskingPattern;

@Component
@ConditionalOnProperty(name = "logging.configuration.logger", havingValue = "custom")
public class CustomLogger {

  private static final String PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread][%X{traceId:-},%X{spanId:-}] %logger{40} - %msg%n";

  private static final String CUSTOM_LOGGER_NAME = "CUSTOM";

  public CustomLogger(final LoggingConfigurationProperties properties) {
    Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.detachAndStopAllAppenders();

    LoggerContext loggerContext = logger.getLoggerContext();
    LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();

    PatternLayout layout = properties.getMask() != null && properties.getMask().isEnabled() ?
        new ConsoleMasking(new MaskingPattern(properties)) :
        new PatternLayout();
    layout.setPattern(PATTERN);
    layout.setContext(loggerContext);
    layout.start();
    encoder.setLayout(layout);

    ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    appender.setEncoder(encoder);
    appender.setTarget(SystemOut.name());
    appender.setName(CUSTOM_LOGGER_NAME);
    appender.setContext(loggerContext);
    appender.start();

    logger.addAppender(appender);
  }
}
