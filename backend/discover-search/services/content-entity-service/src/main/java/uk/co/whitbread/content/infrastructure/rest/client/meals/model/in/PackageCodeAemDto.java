package uk.co.whitbread.content.infrastructure.rest.client.meals.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCodeAemDto {

  private String id;
  private String name;
  private String description;
  private List<String> images;
  private List<AttachmentsAemDto> attachments;
}