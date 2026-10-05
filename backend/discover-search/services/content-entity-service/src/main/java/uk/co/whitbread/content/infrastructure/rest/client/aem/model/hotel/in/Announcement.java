package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

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