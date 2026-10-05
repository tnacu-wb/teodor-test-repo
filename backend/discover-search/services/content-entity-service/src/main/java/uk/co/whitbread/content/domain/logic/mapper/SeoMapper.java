package uk.co.whitbread.content.domain.logic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.domain.model.seo.out.SeoResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SeoMapper {

  @Mapping(target = "pageTitle", source = "searchResultsData.content.seo.pageTitle")
  @Mapping(target = "pageDescription", source = "searchResultsData.content.seo.pageDescription")
  @Mapping(target = "cardImageUrl", source = "searchResultsData.content.seo.cardImageUrl")
  @Mapping(target = "hreflangs", source = "searchResultsData.content.seo.hreflangs")
  @Mapping(target = "faviconUrl", source = "indexHeaderData.content.favicon.faviconUrl")
  @Mapping(target = "icons", source = "indexHeaderData.content.favicon.icons")
  @Mapping(target = "msIcons", source = "indexHeaderData.content.favicon.msIcons")
  SeoResponse toSrpSeoModel(IndexHeaderData indexHeaderData, SearchResultsData searchResultsData);

  @Mapping(target = "pageTitle", source = "hotelInformation.pageTitle")
  @Mapping(target = "pageDescription", source = "hotelInformation.pageDescription")
  @Mapping(target = "cardImageUrl", source = "indexHeaderData.content.seo.cardImageUrl")
  @Mapping(target = "faviconUrl", source = "indexHeaderData.content.favicon.faviconUrl")
  @Mapping(target = "icons", source = "indexHeaderData.content.favicon.icons")
  @Mapping(target = "msIcons", source = "indexHeaderData.content.favicon.msIcons")
  @Mapping(target = "hreflangs", source = "hotelInformation.seo.hreflangs")
  SeoResponse toHdpSeoModel(IndexHeaderData indexHeaderData, HotelInformation hotelInformation);

  @Mapping(target = "pageTitle", source = "title")
  @Mapping(target = "pageDescription", source = "indexHeaderData.content.seo.pageDescription")
  @Mapping(target = "cardImageUrl", source = "indexHeaderData.content.seo.cardImageUrl")
  @Mapping(target = "faviconUrl", source = "indexHeaderData.content.favicon.faviconUrl")
  @Mapping(target = "icons", source = "indexHeaderData.content.favicon.icons")
  @Mapping(target = "msIcons", source = "indexHeaderData.content.favicon.msIcons")
  SeoResponse toBookingFlowPagesSeoModel(IndexHeaderData indexHeaderData, String title);

  @Mapping(target = "pageTitle", source = "indexHeaderData.content.seo.pageTitle")
  @Mapping(target = "pageDescription", source = "indexHeaderData.content.seo.pageDescription")
  @Mapping(target = "cardImageUrl", source = "indexHeaderData.content.seo.cardImageUrl")
  @Mapping(target = "faviconUrl", source = "indexHeaderData.content.favicon.faviconUrl")
  @Mapping(target = "icons", source = "indexHeaderData.content.favicon.icons")
  @Mapping(target = "msIcons", source = "indexHeaderData.content.favicon.msIcons")
  SeoResponse toGeneralPageModel(IndexHeaderData indexHeaderData);

}
