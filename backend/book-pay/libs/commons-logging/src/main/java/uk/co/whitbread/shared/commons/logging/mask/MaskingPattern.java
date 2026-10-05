package uk.co.whitbread.shared.commons.logging.mask;

import org.springframework.core.io.ClassPathResource;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MaskingPattern {

  private static final String PATTERNS_PATH = "masking-patterns.rgx";
  private static final String VAR_PATTERN = "<var>";
  private Pattern multilinePattern;

  public MaskingPattern(LoggingConfigurationProperties properties) {
    try (final InputStream patternsStream =
             new ClassPathResource(PATTERNS_PATH).getInputStream()) {
      BufferedReader patternsBuffer = new BufferedReader(new InputStreamReader(patternsStream));
      List<String> patterns = new ArrayList<>();
      while (patternsBuffer.ready()) {
        patterns.add(patternsBuffer.readLine());
      }
      final String patternString = patterns.stream().filter(pattern -> !pattern.isBlank())
          .collect(Collectors.joining("|"));
      Set<String> fields = properties.getMask() != null ? properties.getMask().getFields() : null;
      if (fields != null) {
        multilinePattern = Pattern.compile(
            fields.stream().filter(field -> !field.isBlank())
                .map(variable -> patternString.replaceAll(VAR_PATTERN, variable))
                .collect(Collectors.joining("|")),
            Pattern.MULTILINE);
      }
    } catch (IOException ex) {
      //no file with masking patterns found
    }
  }

  public String maskMessage(String message) {
    if (multilinePattern == null) {
      return message;
    }
    StringBuilder sb = new StringBuilder(message);
    Matcher matcher = multilinePattern.matcher(sb);
    while (matcher.find()) {
      IntStream.rangeClosed(1, matcher.groupCount())
          .forEach(group -> {
            if (matcher.group(group) != null) {
              IntStream.range(matcher.start(group), matcher.end(group))
                  .forEach(i -> sb.setCharAt(i, '*'));
            }
          });
    }
    return sb.toString();
  }
}