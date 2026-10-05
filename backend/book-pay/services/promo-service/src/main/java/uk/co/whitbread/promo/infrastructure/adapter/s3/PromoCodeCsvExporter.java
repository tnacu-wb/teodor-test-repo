package uk.co.whitbread.promo.infrastructure.adapter.s3;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Component;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;

@Component
public class PromoCodeCsvExporter {

  public byte[] export(List<PromoCodeEntity> codes) {
    StringBuilder sb = new StringBuilder("code\n");

    for (PromoCodeEntity code : codes) {
      sb.append(code.getCode())
          .append("\n");
    }

    return sb.toString().getBytes(StandardCharsets.UTF_8);
  }
}
