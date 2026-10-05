package uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CdhToken {
  private String value;
}
