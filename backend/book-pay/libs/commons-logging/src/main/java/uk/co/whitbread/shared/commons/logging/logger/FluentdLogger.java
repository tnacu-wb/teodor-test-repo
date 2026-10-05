package uk.co.whitbread.shared.commons.logging.logger;

import static ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.composite.LogstashVersionJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggerNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventFormattedTimestampJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider;
import net.logstash.logback.composite.loggingevent.LoggingEventThreadNameJsonProvider;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;
import net.logstash.logback.composite.loggingevent.MdcJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.StackTraceJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import net.logstash.logback.mask.MaskingJsonGeneratorDecorator;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;
import uk.co.whitbread.shared.commons.logging.mask.LogstashMasking;
import uk.co.whitbread.shared.commons.logging.mask.MaskingPattern;

@Component
@ConditionalOnProperty(name = "logging.configuration.logger", havingValue = "fluentd",
    matchIfMissing = true)
public class FluentdLogger {

  private static final String FLUENTD_LOGGER_NAME = "FLUENTD";

  private static final String APPLICATION_NAME = "spring.application.name";

  private static final String FLUENTD_PATTERN = "{ \"appName\": \"%s\" }";

  public FluentdLogger(final LoggingConfigurationProperties properties,
      final Environment environment) {
    Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    logger.detachAndStopAllAppenders();

    LoggerContext loggerContext = logger.getLoggerContext();

    LoggingEventCompositeJsonEncoder encoder = new LoggingEventCompositeJsonEncoder();
    JsonProviders<ILoggingEvent> providers = encoder.getProviders();
    if (properties.getMask() != null && properties.getMask().isEnabled()) {
      MaskingJsonGeneratorDecorator mask = new MaskingJsonGeneratorDecorator();
      mask.addValueMasker(new LogstashMasking(new MaskingPattern(properties)));
      mask.start();
      encoder.setJsonGeneratorDecorator(mask);
    }
    providers.addProvider(
        withPattern(String.format(FLUENTD_PATTERN,
            environment.getProperty(APPLICATION_NAME))));

    StackTraceJsonProvider stackTraceJsonProvider = new StackTraceJsonProvider();
    ShortenedThrowableConverter shortenedThrowableConverter = new ShortenedThrowableConverter();
    shortenedThrowableConverter.setMaxDepthPerThrowable(30);
    shortenedThrowableConverter.setMaxLength(2046);
    shortenedThrowableConverter.setShortenedClassNameLength(20);
    shortenedThrowableConverter.setRootCauseFirst(true);
    stackTraceJsonProvider.setThrowableConverter(shortenedThrowableConverter);
    providers.addProvider(stackTraceJsonProvider);

    providers.addProvider(new LoggingEventFormattedTimestampJsonProvider());
    providers.addProvider(new LogstashVersionJsonProvider<>());
    providers.addProvider(new MdcJsonProvider());
    providers.addProvider(new MessageJsonProvider());
    providers.addProvider(new LogLevelJsonProvider());
    providers.addProvider(new LoggerNameJsonProvider());
    providers.addProvider(new LoggingEventThreadNameJsonProvider());
    providers.addProvider(new LogstashMarkersJsonProvider());
    providers.addProvider(new StackTraceJsonProvider());
    providers.setJsonFactory(new MappingJsonFactory());
    providers.setContext(loggerContext);
    providers.start();

    encoder.setProviders(providers);
    encoder.setContext(loggerContext);
    encoder.start();

    ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
    consoleAppender.setEncoder(encoder);
    consoleAppender.setTarget(SystemOut.getName());
    consoleAppender.setName(FLUENTD_LOGGER_NAME);
    consoleAppender.setContext(loggerContext);
    consoleAppender.start();

    logger.addAppender(consoleAppender);
  }

  private JsonProvider<ILoggingEvent> withPattern(String format) {
    LoggingEventPatternJsonProvider provider = new LoggingEventPatternJsonProvider();
    provider.setPattern(format);
    return provider;
  }
}