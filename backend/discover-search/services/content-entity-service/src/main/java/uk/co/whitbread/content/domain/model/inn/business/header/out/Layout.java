package uk.co.whitbread.content.domain.model.inn.business.header.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Layout {

  private ManageAccount manageAccount;
  private Menu menu;
  private Help help;
  private Sidebar sidebar;

}
