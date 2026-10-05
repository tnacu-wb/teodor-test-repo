package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorldlineCardDetails {

  private String cardHolderName;
  private Integer cardLimit;
  private int cardId;
  private String cardNumber;
  private boolean isMyCard;
  private boolean isActivated;
  private String status;
  private int primaryUserId;
  private int userId;
  private Date expiryDate;
  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String cardAction;
  private List<WorldlineRegisteredUser> registeredUsers;
  private WorldlineCardRestriction cardRestriction;
  private WorldlineCardAmountSpend amountSpend;

}
