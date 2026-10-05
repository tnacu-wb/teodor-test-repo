package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out;

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

  private String imageSrc;
  private String name;
  private String description;
  private String informationBox;
  private List<String> charityCodes;

}
