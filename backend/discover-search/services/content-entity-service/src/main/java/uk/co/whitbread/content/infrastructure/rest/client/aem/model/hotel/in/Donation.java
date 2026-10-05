package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

  private String rateCode;
  private String imagePath;
  private String imageFullPath;
  private String title;
  private String info;
  private List<Amount> amounts;
}