package uk.co.whitbread.content.domain.model.dlp.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DlpInformation {

  private Seo seo;
  private List<Breadcrumb> breadcrumbs;
  private String title;
  private String description;
  private String picture;
  private List<Hotel> hotels;
  private Why why;
  private Coordinates coordinates;
  private List<Faq> faq;
  private Dlp dlps;
  private List<Promo> promos;
  private ThingsToDo thingsToDo;
}
