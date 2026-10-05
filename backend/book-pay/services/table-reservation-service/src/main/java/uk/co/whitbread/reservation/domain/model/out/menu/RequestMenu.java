package uk.co.whitbread.reservation.domain.model.out.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMenu {

  private String from;

  private String until;

  private String time;

  private Integer adults;

  private Integer children;

  private String occasionId;

  private Boolean includeUnavailable;

  private String companyId;

  private String siteId;

}
