package uk.co.whitbread.promo.domain.ports.secondary;

import java.util.UUID;

public interface PromoBatchCodeGeneratorOutPort {

  void generateCodes(UUID batchId, int count, String prefix, int codeLen);
}
