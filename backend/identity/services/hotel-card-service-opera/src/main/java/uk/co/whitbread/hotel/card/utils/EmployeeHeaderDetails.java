package uk.co.whitbread.hotel.card.utils;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeHeaderDetails {

  private final String sessionId;
  private final String companyId;
  private final String employeeId;
}
