package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationDto {

  private String title;
  private String description;
  private String imagePath;
  private String informationBox;
  private List<String> charityCodes;

}
