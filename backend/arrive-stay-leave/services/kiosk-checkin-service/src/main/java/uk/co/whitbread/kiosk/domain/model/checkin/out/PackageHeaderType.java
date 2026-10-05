package uk.co.whitbread.kiosk.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageHeaderType {

  private PrimaryDetails primaryDetails;
  private TransactionDetails transactionDetails;
  private PostingAttributes postingAttributes;
  private UsageDetails usageDetails;

}
