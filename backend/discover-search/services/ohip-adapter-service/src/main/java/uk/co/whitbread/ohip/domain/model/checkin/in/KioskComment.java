package uk.co.whitbread.ohip.domain.model.checkin.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class KioskComment {

  private String commentTitle;
  private Boolean internal;
  private String notificationLocation;
  private CommentText text;
  private String type;

}
