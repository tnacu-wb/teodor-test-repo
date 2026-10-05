package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {

  private String title;
  private String announcementSource;
  private String showAnnouncement;
  private String startDate;
  private String endDate;
  private String fullBannerLink;
  private String fullBannerLinkNewTab;
  private String icon;
  private String text;
  private String bbText;
  private String type;


}
