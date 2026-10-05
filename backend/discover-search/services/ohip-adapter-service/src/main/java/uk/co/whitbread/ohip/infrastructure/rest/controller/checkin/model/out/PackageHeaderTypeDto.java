package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageHeaderTypeDto {

  private CheckInPrimaryDetailsDto primaryDetails;
  private TransactionDetailsDto transactionDetails;
  private PostingAttributesDto postingAttributes;
  private UsageDetailsDto usageDetails;

}
