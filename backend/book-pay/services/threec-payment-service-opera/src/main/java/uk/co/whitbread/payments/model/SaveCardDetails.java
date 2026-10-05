package uk.co.whitbread.payments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class SaveCardDetails {
  private Address billingAddress;
  private boolean business;
  private boolean personalCard;
  private boolean cnpRequired;
  private String memorableWord;
  private String cardId;
  private String cardLabel;
  private String email;
  private String accountId;
  private String companyAccountId;
  private String employeeAccountId;
  private String environment;
  private String language;
}
