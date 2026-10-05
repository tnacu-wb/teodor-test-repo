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
public class WorldlineCancelAndReplaceCardDetails {

  private String cardHolderName;
  private WorldlineCardRestriction cardRestriction;
  private String cardId;
  private String pan;
  private String primarySchemeCustomerId;
  private boolean isMyCard;
  private String status;
  private boolean isActivated;
  private Date expiryDate;
  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private List<WorldlineRegisteredUser> registeredUsers;
}
