package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ContentDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderContentResponseAemDto {

  private ContentDto content;

}
