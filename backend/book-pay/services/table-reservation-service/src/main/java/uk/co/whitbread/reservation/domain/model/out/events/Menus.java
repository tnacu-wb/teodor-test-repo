package uk.co.whitbread.reservation.domain.model.out.events;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menus {

  private String id;

  private String name;

  private List<OrderMenu> iOrderMenus = new ArrayList<>();

}
