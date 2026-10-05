package uk.co.whitbread.shared.cdh.oauth.token;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CdhToken {
  private String value;
}
