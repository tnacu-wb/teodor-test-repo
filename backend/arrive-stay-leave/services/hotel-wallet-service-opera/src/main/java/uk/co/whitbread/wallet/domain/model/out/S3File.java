package uk.co.whitbread.wallet.domain.model.out;

import java.io.InputStream;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class S3File {

  private String key;
  private InputStream data;
}
