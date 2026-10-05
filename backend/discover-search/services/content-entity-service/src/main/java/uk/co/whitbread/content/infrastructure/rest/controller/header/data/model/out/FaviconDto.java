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
public class FaviconDto {

  private String faviconUrl;
  private List<IconDto> icons;
  private List<MsIconDto> msIcons;
}
