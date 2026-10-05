package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum AccessLevel {

  SUPER(9),
  BOOKER(9),
  SELF(1),
  STAYER(0);

  AccessLevel(int maxReservations) {
    this.maxReservations = maxReservations;
  }

  private static final Map<String, AccessLevel> accessLevelIndex = new HashMap<>();

  static {
    for (AccessLevel accessLevel : AccessLevel.values()) {
      accessLevelIndex.put(accessLevel.name(), accessLevel);
    }
  }

  private final int maxReservations;

  public static Optional<AccessLevel> lookupByName(String name) {
    return Optional.ofNullable(accessLevelIndex.get(StringUtils.upperCase(name)));
  }
}
