package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceFinderViewsDto {
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
  private List<HotelCodesDto> hotelCodes;
  private List<DestinationsDto> destinations;
  private List<InfoMessagesDto> infoMessages;
  private String seoMetaTitle;
  private String seoMetaDescription;
  private String seoCardImageUrl;
  private String seoRobots;
  private List<SeoHreflangsDto> seoHreflangs;

}
