package uk.co.whitbread.content.infrastructure.config;

import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.co.whitbread.content.domain.logic.BookingInPortImpl;
import uk.co.whitbread.content.domain.logic.BusinessNotesInPortImpl;
import uk.co.whitbread.content.domain.logic.CardManagementInPortImpl;
import uk.co.whitbread.content.domain.logic.ContentInPortImpl;
import uk.co.whitbread.content.domain.logic.CookiePoliciesInPortImpl;
import uk.co.whitbread.content.domain.logic.CountriesInPortImpl;
import uk.co.whitbread.content.domain.logic.FooterInPortImpl;
import uk.co.whitbread.content.domain.logic.HeaderInPortImpl;
import uk.co.whitbread.content.domain.logic.MealsInPortImpl;
import uk.co.whitbread.content.domain.logic.PageDataInPortImpl;
import uk.co.whitbread.content.domain.logic.RoomTypeInPortImpl;
import uk.co.whitbread.content.domain.logic.SeoInPortImpl;
import uk.co.whitbread.content.domain.logic.mapper.HotelInformationExtendedMapper;
import uk.co.whitbread.content.domain.logic.mapper.SeoMapper;
import uk.co.whitbread.content.domain.model.feature.FeatureFlag;
import uk.co.whitbread.content.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.content.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.content.domain.ports.primary.BookingInPort;
import uk.co.whitbread.content.domain.ports.primary.BusinessNotesInPort;
import uk.co.whitbread.content.domain.ports.primary.CardManagementInPort;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.domain.ports.primary.CookiePoliciesInPort;
import uk.co.whitbread.content.domain.ports.primary.CountriesInPort;
import uk.co.whitbread.content.domain.ports.primary.FooterInPort;
import uk.co.whitbread.content.domain.ports.primary.HeaderInPort;
import uk.co.whitbread.content.domain.ports.primary.MealsInPort;
import uk.co.whitbread.content.domain.ports.primary.PageDataInPort;
import uk.co.whitbread.content.domain.ports.primary.RoomTypeInPort;
import uk.co.whitbread.content.domain.ports.primary.SeoInPort;
import uk.co.whitbread.content.domain.ports.secondary.BookingOutPort;
import uk.co.whitbread.content.domain.ports.secondary.BusinessNotesOutPort;
import uk.co.whitbread.content.domain.ports.secondary.CardManagementOutPort;
import uk.co.whitbread.content.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.content.domain.ports.secondary.CookiePoliciesOutPort;
import uk.co.whitbread.content.domain.ports.secondary.CountriesOutPort;
import uk.co.whitbread.content.domain.ports.secondary.FooterOutPort;
import uk.co.whitbread.content.domain.ports.secondary.HeaderOutPort;
import uk.co.whitbread.content.domain.ports.secondary.MealsOutPort;
import uk.co.whitbread.content.domain.ports.secondary.OhipOutPort;
import uk.co.whitbread.content.domain.ports.secondary.PageDataOutPort;
import uk.co.whitbread.content.domain.ports.secondary.RoomTypeOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.AemClientAppsHomepageMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.AemClientDlpInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.GlobalConfigRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.HotelInfoMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.HotelInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.HotelInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.IndexHeaderDataMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.IndexHeaderDataRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.PriceFinderGlobalConfigMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.PromotionsConfigMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.RoomClassConfigMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.RoomUpgradeOptionsMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.SearchResultsDataMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.SearchRulesMapper;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.UpsellItemsExtrasMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.BookingOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.booking.aem.adapter.BookingAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.BookingInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.RateInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.client.booking.mapper.RateInformationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.ContentOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.HotelReviewClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LocalizationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.MultipleLabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.resolver.PromoBoxResolver;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoFlowTypeResolver;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoStrategyRegistry;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.CookiePoliciesOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.adapter.CookiePoliciesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper.CookiePoliciesMapper;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper.CookiePoliciesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.countries.CountriesOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.countries.aem.adapter.CountriesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.countries.mapper.CountriesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.countries.mapper.CountriesResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.footer.FooterOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.footer.adapter.FooterAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.footer.mapper.FooterRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.footer.mapper.FooterResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.CardManagementOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.adapter.CardManagementAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CardManagementRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CardManagementResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CommonIconsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CommonIconsResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.HeaderOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.adapter.HeaderAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.HeaderContentRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.HeaderContentResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.LayoutRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.LayoutResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.PageDataOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.aem.PageDataAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.mapper.PageDataRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.meals.MealsOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.meals.adapter.MealsAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.meals.mapper.MealsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.meals.mapper.MealsResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.BusinessNotesOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.adapter.BusinessNotesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper.BusinessNotesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper.BusinessNotesResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.OhipAdapterClient;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.OhipOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.RoomTypeOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.adapter.RoomTypeAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeMapper;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.SnowdropHotelsRetriever;

@Configuration
@EnableScheduling
@EnableAsync
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public ContentInPort getContentInPort(LabelsRequestMapper labelsRequestMapper,
                                        LocalizationRequestMapper localizationRequestMapper,
                                        MultipleLabelsRequestMapper multipleLabelsRequestMapper,
                                        HotelInformationRequestMapper hotelInformationRequestMapper,
                                        HotelInformationMapper hotelInformationMapper,
                                        IndexHeaderDataMapper indexHeaderDataMapper,
                                        SearchResultsDataMapper searchResultsDataMapper,
                                        SearchRulesMapper searchRulesMapper,
                                        GlobalConfigRequestMapper globalConfigRequestMapper,
                                        RoomClassConfigMapper roomClassConfigMapper,
                                        AemClientDlpInformationMapper dlpInformationMapper,
                                        AemClientAppsHomepageMapper homepageMapper,
                                        IndexHeaderDataRequestMapper indexHeaderDataRequestMapper,
                                        RoomUpgradeOptionsMapper roomUpgradeOptionsMapper,
                                        PromotionsConfigMapper promotionsConfigMapper,
                                        PriceFinderGlobalConfigMapper priceFinderGlobalConfigMapper,
                                        AemClient aemClient,
                                        SrpFiltersConfig srpFiltersConfig,
                                        OhipAdapterClient ohipAdapterClient,
                                        HotelInfoMapper hotelInfoMapper,
                                        HotelInformationExtendedMapper hotelInformationExtendedMapper,
                                        HotelReviewClient hotelReviewClient,
                                        @Qualifier("cacheManager1Day") CacheManager cacheManager,
                                        UnleashWrapper<FeatureFlag> unleashWrapper,
                                        SnowdropHotelsRetriever snowdropHotelsRetriever,
                                        PromoFlowTypeResolver flowTypeResolver,
                                        PromoStrategyRegistry strategyRegistry,
                                        PromoBoxResolver promoBoxResolver,
                                        UpsellItemsExtrasMapper upsellItemsExtrasMapper) {
    return new ContentInPortImpl(
        getContentOutPort(labelsRequestMapper,
            localizationRequestMapper,
            multipleLabelsRequestMapper,
            hotelInformationRequestMapper,
            hotelInformationMapper, indexHeaderDataMapper, searchResultsDataMapper,
            searchRulesMapper,
            globalConfigRequestMapper, roomClassConfigMapper, dlpInformationMapper,
            homepageMapper, indexHeaderDataRequestMapper, roomUpgradeOptionsMapper,
            promotionsConfigMapper, priceFinderGlobalConfigMapper, upsellItemsExtrasMapper, aemClient,
            hotelReviewClient, srpFiltersConfig, unleashWrapper, snowdropHotelsRetriever,
            flowTypeResolver, strategyRegistry, promoBoxResolver),
        getOhipOutPort(ohipAdapterClient, hotelInfoMapper),
        hotelInformationExtendedMapper,
        cacheManager);
  }

  @Bean
  public ContentOutPort getContentOutPort(LabelsRequestMapper labelsRequestMapper,
                                          LocalizationRequestMapper localizationRequestMapper,
                                          MultipleLabelsRequestMapper multipleLabelsRequestMapper,
                                          HotelInformationRequestMapper hotelInformationRequestMapper,
                                          HotelInformationMapper hotelInformationMapper,
                                          IndexHeaderDataMapper indexHeaderDataMapper,
                                          SearchResultsDataMapper searchResultsDataMapper,
                                          SearchRulesMapper searchRulesMapper,
                                          GlobalConfigRequestMapper globalConfigRequestMapper,
                                          RoomClassConfigMapper roomClassConfigMapper,
                                          AemClientDlpInformationMapper dlpInformationMapper,
                                          AemClientAppsHomepageMapper homepageMapper,
                                          IndexHeaderDataRequestMapper indexHeaderDataRequestMapper,
                                          RoomUpgradeOptionsMapper roomUpgradeOptionsMapper,
                                          PromotionsConfigMapper promotionsConfigMapper,
                                          PriceFinderGlobalConfigMapper priceFinderConfigMapper,
                                          UpsellItemsExtrasMapper upsellItemsExtrasMapper,
                                          AemClient aemClient, HotelReviewClient hotelReviewClient,
                                          SrpFiltersConfig srpFiltersConfig,
                                          UnleashWrapper<FeatureFlag> unleashWrapper,
                                          SnowdropHotelsRetriever snowdropHotelsRetriever,
                                          PromoFlowTypeResolver flowTypeResolver,
                                          PromoStrategyRegistry strategyRegistry,
                                          PromoBoxResolver promoBoxResolver) {
    return new ContentOutPortImpl(labelsRequestMapper, localizationRequestMapper,
        multipleLabelsRequestMapper, hotelInformationRequestMapper,
        hotelInformationMapper, indexHeaderDataMapper, searchResultsDataMapper,
        searchRulesMapper, indexHeaderDataRequestMapper, globalConfigRequestMapper,
        roomClassConfigMapper, dlpInformationMapper, homepageMapper, roomUpgradeOptionsMapper, aemClient,
        hotelReviewClient, srpFiltersConfig, unleashWrapper, snowdropHotelsRetriever,
        promotionsConfigMapper, priceFinderConfigMapper, flowTypeResolver, strategyRegistry,
        promoBoxResolver, upsellItemsExtrasMapper);
  }

  @Bean
  public OhipOutPort getOhipOutPort(OhipAdapterClient ohipAdapterClient,
                                    HotelInfoMapper hotelInfoMapper) {
    return new OhipOutPortImpl(ohipAdapterClient, hotelInfoMapper);
  }

  @Bean
  public BusinessNotesInPort getBusinessNotesInPort(
      final BusinessNotesOutPort businessNotesOutPort) {
    return new BusinessNotesInPortImpl(businessNotesOutPort);
  }

  @Bean
  public BusinessNotesOutPort getBusinessNotesOutPort(
      final BusinessNotesAemClient businessNotesAemClient,
      final BusinessNotesRequestMapper businessNotesRequestMapper,
      final BusinessNotesResponseMapper businessNotesResponseMapper) {
    return new BusinessNotesOutPortImpl(businessNotesAemClient, businessNotesRequestMapper,
        businessNotesResponseMapper);
  }

  @Bean
  public MealsInPort getMealsInPort(final MealsOutPort mealsOutPort) {
    return new MealsInPortImpl(mealsOutPort);
  }

  @Bean
  public MealsOutPort getMealsOutPort(final MealsRequestMapper mealsRequestMapper,
                                      final MealsResponseMapper mealsResponseMapper,
                                      final MealsAemClient aemClient) {
    return new MealsOutPortImpl(mealsRequestMapper, mealsResponseMapper, aemClient);
  }

  @Bean
  public BookingInPort getBookingInPort(BookingOutPort bookingOutPort) {
    return new BookingInPortImpl(bookingOutPort);
  }

  @Bean
  public BookingOutPort getBookingOutPort(BookingInformationMapper bookingInformationMapper,
                                          BookingInformationRequestMapper bookingInformationRequestMapper,
                                          RateInformationRequestMapper rateInformationRequestMapper,
                                          RateInformationMapper rateInformationMapper,
                                          BookingAemClient aemClient,
                                          OhipAdapterClient ohipAdapterClient,
                                          UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new BookingOutPortImpl(bookingInformationMapper,
        bookingInformationRequestMapper, rateInformationRequestMapper, rateInformationMapper,
        aemClient, ohipAdapterClient, unleashWrapper);
  }

  @Bean
  public HeaderInPort getHeaderInPort(final HeaderOutPort headerOutPort) {
    return new HeaderInPortImpl(headerOutPort);
  }

  @Bean
  public HeaderOutPort getHeaderOutPort(final HeaderAemClient aemClient,
                                        final LayoutRequestMapper layoutRequestMapper, final
                                        LayoutResponseMapper layoutResponseMapper, final HeaderContentRequestMapper
                                            headerContentRequestMapper, final HeaderContentResponseMapper
                                            headerContentResponseMapper) {
    return new HeaderOutPortImpl(aemClient, layoutRequestMapper,
        layoutResponseMapper, headerContentRequestMapper, headerContentResponseMapper);
  }

  @Bean
  public FooterInPort getFooterInPort(final FooterOutPort footerOutPort) {
    return new FooterInPortImpl(footerOutPort);
  }

  @Bean
  public FooterOutPort getFooterOutPort(final FooterRequestMapper footerRequestMapper,
                                        final FooterResponseMapper footerResponseMapper,
                                        final FooterAemClient aemClient) {
    return new FooterOutPortImpl(footerRequestMapper, aemClient, footerResponseMapper);
  }

  @Bean
  public RoomTypeInPort getRoomTypeInPort(RoomTypeOutPort roomTypeOutPort) {
    return new RoomTypeInPortImpl(roomTypeOutPort);
  }

  @Bean
  public RoomTypeOutPort getRoomTypeOutPort(RoomTypeAemClient roomTypeAemClient,
                                            RoomTypeRequestMapper roomTypeRequestMapper,
                                            RoomTypeMapper roomTypeMapper,
                                            AemClient aemClient) {
    return new RoomTypeOutPortImpl(roomTypeAemClient, roomTypeRequestMapper, roomTypeMapper, aemClient);
  }

  @Bean
  public CountriesInPort getCountriesInPort(CountriesOutPort countriesOutPort) {
    return new CountriesInPortImpl(countriesOutPort);
  }

  @Bean
  public CountriesOutPort getCountriesOutPort(CountriesAemClient countriesAemClient,
                                              CountriesRequestMapper countriesRequestMapper,
                                              CountriesResponseMapper countriesResponseMapper) {
    return new CountriesOutPortImpl(countriesAemClient, countriesRequestMapper,
        countriesResponseMapper);
  }

  @Bean
  public CookiePoliciesInPort getCookiePoliciesInPort(
      final CookiePoliciesOutPort cookiePoliciesOutPort) {
    return new CookiePoliciesInPortImpl(cookiePoliciesOutPort);
  }

  @Bean
  public CookiePoliciesOutPort getCookiePoliciesOutPort(
      final CookiePoliciesAemClient cookiePoliciesAemClient,
      final CookiePoliciesRequestMapper cookiePoliciesRequestMapper,
      final CookiePoliciesMapper cookiePoliciesMapper) {
    return new CookiePoliciesOutPortImpl(cookiePoliciesAemClient, cookiePoliciesRequestMapper,
        cookiePoliciesMapper);
  }

  @Bean
  public SeoInPort getSeoInPort(
      final ContentOutPort contentOutPort,
      final BookingOutPort bookingOutPort,
      final SeoMapper seoMapper) {
    return new SeoInPortImpl(contentOutPort, bookingOutPort, seoMapper);
  }

  @Bean
  public CardManagementInPort getCardManagementInPort(final CardManagementOutPort cardManagementOutPort) {
    return new CardManagementInPortImpl(cardManagementOutPort);
  }

  @Bean
  public CardManagementOutPort getCardManagementOutPort(final CardManagementAemClient aemClient,
                                                        final CommonIconsRequestMapper commonIconsRequestMapper, final
                                                        CommonIconsResponseMapper commonIconsResponseMapper,
                                                        final CardManagementRequestMapper
                                                            cardManagementRequestMapper,
                                                        final CardManagementResponseMapper
                                                            cardManagementResponseMapper) {
    return new CardManagementOutPortImpl(aemClient, commonIconsRequestMapper,
        commonIconsResponseMapper, cardManagementRequestMapper, cardManagementResponseMapper);
  }

  @Bean
  public PageDataInPort getPageDataInPort(final PageDataOutPort pageDataOutPort) {
    return new PageDataInPortImpl(pageDataOutPort);
  }

  @Bean
  PageDataOutPort getPageDataOutPort(final PageDataRequestMapper pageDataRequestMapper,
                                     final PageDataAemClient pageDataAemClient) {
    return new PageDataOutPortImpl(pageDataRequestMapper, pageDataAemClient);
  }
}

