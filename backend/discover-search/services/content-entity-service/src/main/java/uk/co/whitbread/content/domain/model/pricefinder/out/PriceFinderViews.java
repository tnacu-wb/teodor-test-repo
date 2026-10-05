package uk.co.whitbread.content.domain.model.pricefinder.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceFinderViews {
  private String path;
  private String bannerImage;
  private String bannerColour;
  private String bannerHeadline;
  private String bannerSubtext;
  private String termsLabel;
  private String dateRangeStart;
  private String dateRangeEnd;
  private String checkinDate;
  private Integer highlightedPriceRangeStep;
  private Integer highlightedPriceRangeMin;
  private Integer highlightedPriceRangeMax;
  private String highlightedPricePrimaryColour;
  private String highlightedPriceSecondaryColour;
  private String filterRoomSelected;
  private String locationId;
  private String locationName;
  private Double locationRadius;
  private List<HotelCodes> hotelCodes;
  private List<Destinations> destinations;
  private List<InfoMessages> infoMessages;
  private String seoMetaTitle;
  private String seoMetaDescription;
  private String seoCardImageUrl;
  private String seoRobots;
  private List<SeoHreflangs> seoHreflangs;
}
