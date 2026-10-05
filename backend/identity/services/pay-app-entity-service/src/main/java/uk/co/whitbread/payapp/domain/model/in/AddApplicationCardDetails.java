package uk.co.whitbread.payapp.domain.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddApplicationCardDetails extends DomainValidator<AddApplicationCardDetails> {

  private Integer cardLimit;
  @NotBlank
  private String cardName;
  private boolean myCard;
  private String title;
  private String foreName;
  private String lastName;
  private String emailAddress;
  private boolean restrictCardUsage;
  private Date startDate;
  private Date endDate;
  private Boolean isConsentGiven;

  @SuppressWarnings("java:S107")
  public AddApplicationCardDetails(Integer cardLimit, String cardName, boolean myCard,
      String title, String foreName, String lastName, String emailAddress, boolean restrictCardUsage,
      Date startDate, Date endDate, Boolean isConsentGiven) {
    this.cardLimit = cardLimit;
    this.cardName = cardName;
    this.myCard = myCard;
    this.title = title;
    this.foreName = foreName;
    this.lastName = lastName;
    this.emailAddress = emailAddress;
    this.restrictCardUsage = restrictCardUsage;
    this.startDate = startDate;
    this.endDate = endDate;
    this.isConsentGiven = isConsentGiven;
    this.validateSelf();
  }
}
