package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddApplicationCardDetailsDto extends ModelValidator<AddApplicationCardDetailsDto> {

  private Integer cardLimit;
  private String cardName;
  private boolean myCard;
  private String title;
  private String foreName;
  private String lastName;
  private String emailAddress;
  private boolean restrictCardUsage;
  private Date startDate;
  private Date endDate;


  @SuppressWarnings("java:S107")
  public AddApplicationCardDetailsDto(Integer cardLimit, String cardName, boolean myCard,
      String title, String foreName, String lastName, String emailAddress, boolean restrictCardUsage,
      Date startDate, Date endDate) {
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
    this.validate();
  }
}
