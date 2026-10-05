package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BusinessDto {
  private String accessLevel;
  private Long awaitingApproval;
  private String centralCard;
  private String customerReferenceAnswer;
  private Boolean dismissMpiLink;
  private String employeeId;
  private Boolean miSetupRequired;
  @JsonProperty("myPILink")
  private String myPiLink;
  private String purchaseOrderAnswer;
}
