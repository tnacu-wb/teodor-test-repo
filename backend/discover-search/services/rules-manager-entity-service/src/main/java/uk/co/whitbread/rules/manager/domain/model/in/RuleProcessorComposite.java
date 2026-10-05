package uk.co.whitbread.rules.manager.domain.model.in;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
public class RuleProcessorComposite<T extends Rule> {

  private final T recordToUpdate;
  private final Integer recordIdToDelete;

}
