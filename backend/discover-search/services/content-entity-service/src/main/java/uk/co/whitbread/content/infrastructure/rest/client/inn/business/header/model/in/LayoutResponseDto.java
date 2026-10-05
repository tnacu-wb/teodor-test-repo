package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HelpDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ManageAccountDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.MenuDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.SidebarDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutResponseDto {

  private ManageAccountDto manageAccount;
  private MenuDto menu;
  private HelpDto help;
  private SidebarDto sidebar;

}
