package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileStayingGuestDetailsDto {

  private List<GuestDetailsDto> guestDetails;

}
