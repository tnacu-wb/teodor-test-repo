package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlpInformationDto {

  private @NotNull SeoDto seo;
  private List<@NotNull BreadcrumbDto> breadcrumbs;
  private @NotNull String title;
  private String description;
  private String image;
  private List<HotelDto> hotels;
  private WhyDto why;
  private CoordinatesDto map;
  private List<FaqDto> faqs;
  private DlpDto dlps;
  private List<PromoDto> promos;
  private ThingsToDoDto thingsToDo;
}
