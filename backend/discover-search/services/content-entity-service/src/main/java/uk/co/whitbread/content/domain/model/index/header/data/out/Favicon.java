package uk.co.whitbread.content.domain.model.index.header.data.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favicon {

  private String faviconUrl;
  private List<Icon> icons;
  private List<MsIcon> msIcons;
}
