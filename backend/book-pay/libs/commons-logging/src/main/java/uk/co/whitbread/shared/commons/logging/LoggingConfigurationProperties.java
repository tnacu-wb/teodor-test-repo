package uk.co.whitbread.shared.commons.logging;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "logging.configuration", ignoreUnknownFields = false)
@Getter
@Setter
public class LoggingConfigurationProperties {

  /**
   * Set console logger. If not specified, defaults to fluentd which encodes logs into JSON format.
   * <p>
   * Other options are:
   * <ul>
   *   <li> custom - custom logging pattern, masking can be applied</li>
   *   <li> local - application's default logging pattern, masking cannot be applied.</li>
   * </ul>
   */
  private String logger;
  private Boolean trace;
  private MaskProperties mask;
  private DebugProperties debug;

  @Getter
  @Setter
  public static class MaskProperties {

    /**
     * Enable logging masking
     */
    private boolean enabled;
    /**
     * Masking fields list
     */
    private Set<String> fields;
  }

  @Getter
  @Setter
  public static class DebugProperties {

    /**
     * Exclude URIs from being logged. This list applies strictly to uri and status properties.
     */
    private Set<String> exclusions;
    private boolean enabled;
    private HTTPMessage request;
    private HTTPMessage response;
  }

  @Getter
  @Setter
  public static class HTTPMessage {

    /**
     * Parts of HTTP message that get logged.
     */
    private boolean body;
  }

}