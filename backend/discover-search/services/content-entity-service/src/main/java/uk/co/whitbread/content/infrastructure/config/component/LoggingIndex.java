package uk.co.whitbread.content.infrastructure.config.component;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoggingIndex {

  private String remoteService;
  private String url;
  private String path;
  private String httpMethod;
  private Long duration;
}
