package uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;


@Slf4j
@Configuration
public class JacksonConfig {

  private static final String TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final String DATE_FORMAT = "yyyy-MM-dd";

  private static final Pattern TIMESTAMP_PATTERN =
      Pattern.compile("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?$");

  @Bean
  @Primary
  public JsonMapper objectMapper() {
    SimpleModule customDateModule = new SimpleModule("CustomDateModule");
    customDateModule.addSerializer(Date.class, dateSerializer());
    customDateModule.addDeserializer(Date.class, dateDeserializer());
    // Jackson 3 enforces standard bean naming: getters/setters must start with
    // uppercase after "get"/"set". Generated OpenAPI models have non-standard
    // accessors like getaRAccount()/setaRAccount(), so we enable field visibility
    // to ensure all properties are detected via their fields.
    return JsonMapper.builder()
        .addModule(customDateModule)
        .changeDefaultVisibility(vc ->
            vc.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
        .changeDefaultPropertyInclusion(
            incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
        .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
  }

  private ValueDeserializer<Date> dateDeserializer() {
    return new ValueDeserializer<>() {
      @Override
      public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt)
          throws JacksonException {
        final JsonNode node = ctxt.readTree(jsonParser);
        if (node.textValue() != null) {
          final String date = node.textValue();
          final var matcher = TIMESTAMP_PATTERN.matcher(date);
          final var sdf = matcher.matches() ? new SimpleDateFormat(TIMESTAMP_DATE_FORMAT)
              : new SimpleDateFormat(DATE_FORMAT);
          try {
            return sdf.parse(date);
          } catch (ParseException e) {
            log.error("Failed to parse", e);
            log.debug("Date \"{}\" is not compatible with format: {}", date, sdf.toPattern());
            throw new IllegalArgumentException(
                "Could not parse date: \"" + date + "\". Supported formats: "
                    + TIMESTAMP_DATE_FORMAT + ", " + DATE_FORMAT, e);
          }
        } else if (node.longValue() != 0L) {
          return new Date(node.longValue());
        }
        throw new IllegalArgumentException("Could not parse date");
      }
    };
  }

  private ValueSerializer<Date> dateSerializer() {
    return new ValueSerializer<>() {
      private final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
      private final DateFormat dtf = new SimpleDateFormat(TIMESTAMP_DATE_FORMAT);

      @Override
      public void serialize(Date date, JsonGenerator jsonGenerator,
          SerializationContext ctxt) throws JacksonException {
        if (DateUtils.truncate(date, Calendar.DAY_OF_MONTH).equals(date)) {
          jsonGenerator.writeString(df.format(date));
        } else {
          jsonGenerator.writeString(dtf.format(date));
        }
      }
    };
  }
}