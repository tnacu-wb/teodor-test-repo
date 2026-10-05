package uk.co.whitbread.content.infrastructure.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ContentServiceConfig {

  private final Integer hotelsInformationProcessingThreadCount;

  public ContentServiceConfig(@Value("${content-entity.hotels.information.processing.thread.count}")
                                  Integer hotelsInformationProcessingThreadCount) {
    this.hotelsInformationProcessingThreadCount = hotelsInformationProcessingThreadCount;
  }
}
