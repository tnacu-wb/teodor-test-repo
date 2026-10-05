package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

  private String name;
  private String description;
  private String image;
  private String path;
  private String pathLabel;
  private String disclaimer;
  private String stayStartDate;
  private String stayEndDate;
}