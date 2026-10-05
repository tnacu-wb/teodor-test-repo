package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuRespDto {

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