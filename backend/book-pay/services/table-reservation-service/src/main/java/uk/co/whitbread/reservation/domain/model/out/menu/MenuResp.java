package uk.co.whitbread.reservation.domain.model.out.menu;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResp {

  private String id;
  private String name;
  private String description;
  private String externalUrl;
  private int depositPerAdult;
  private int depositPerChild;
  private boolean available;
  private Integer remainingEvents;
  private Integer remainingCovers;
  private boolean cardGuaranteeAlwaysRequired;
  private Integer cardGuaranteeMinCovers;
  private boolean preAuthAlwaysRequired;
  private Integer preAuthMinCovers;
  private List<String> imageLinks;
  private List<String> iOrderMenus;

}