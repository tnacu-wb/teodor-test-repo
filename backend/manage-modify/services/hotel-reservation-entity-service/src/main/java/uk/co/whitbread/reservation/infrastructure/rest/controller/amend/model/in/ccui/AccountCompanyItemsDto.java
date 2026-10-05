package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCompanyItemsDto {
  private String companyNumber;
  private String charges;
  private String companyId;
}
