package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class Business {

  private String accessLevel;
  private Long awaitingApproval;
  private String centralCard;
  private String customerReferenceAnswer;
  private Boolean dismissMpiLink;
  private String employeeId;
  private Boolean miSetupRequired;
  private String myPiLink;
  private String purchaseOrderAnswer;
}
