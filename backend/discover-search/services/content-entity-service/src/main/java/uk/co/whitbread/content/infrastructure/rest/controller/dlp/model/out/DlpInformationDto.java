package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DlpInformationDto {

  private SeoDto seo;
  private List<BreadcrumbDto> breadcrumbs;
  private String title;
  private String description;
  private String picture;
  private List<HotelDto> hotels;
  private WhyDto why;
  private CoordinatesDto coordinates;
  private List<FaqDto> faq;
  private DlpDto dlps;
  private List<PromoDto> promos;
  private ThingsToDoDto thingsToDo;
}
