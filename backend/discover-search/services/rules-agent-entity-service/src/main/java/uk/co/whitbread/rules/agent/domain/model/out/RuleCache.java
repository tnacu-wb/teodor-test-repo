package uk.co.whitbread.rules.agent.domain.model.out;

import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;

@Data
public class RuleCache<T extends Rule> {

  private ConcurrentHashMap<Integer, T> cachedRules;
}
