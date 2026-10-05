package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLinkDto {

  private String title;
  private String url;
  private String icon;
}
