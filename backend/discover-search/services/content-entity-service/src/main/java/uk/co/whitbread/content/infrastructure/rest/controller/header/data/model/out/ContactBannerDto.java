package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactBannerDto {
  private String type;
  private String text;
  private String date;
  private List<String> enabledPages;
}
