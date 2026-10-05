package uk.co.whitbread.payments.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain-layer configuration for Datatrans card-option eligibility.
 * Constructed in BeanConfig from the infrastructure DataTransProperties binding.
 * Contains no Spring annotations so the domain layer remains infrastructure-free.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTransConfig {

  /**
   * CardOption names that do NOT support Datatrans.
   * A CardOption is Datatrans-eligible if its name is absent from this list.
   */
  private List<String> notSupportedCardOptions = List.of();

  public boolean isDatatransSupported(String cardOptionName) {
    return !notSupportedCardOptions.contains(cardOptionName);
  }
}
