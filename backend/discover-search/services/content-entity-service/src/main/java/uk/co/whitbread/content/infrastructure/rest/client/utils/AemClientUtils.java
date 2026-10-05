package uk.co.whitbread.content.infrastructure.rest.client.utils;

import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_APPS_HOMEPAGE_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_APPS_HOMEPAGE_SUBCHANNEL_EXCEPTION;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemDlpProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemHomepageProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryEnumDto;

@Slf4j
public class AemClientUtils {

  private static final String CHANNEL_PI = "PI";
  private static final String CHANNEL_BB = "BB";
  private static final String CHANNEL_CCUI = "CCUI";
  private static final String CHANNEL_DISTR = "DISTR";
  private static final String CHANNEL_EMPLOYEE = "EMPLOYEE";
  private static final String CHANNEL_TRAVEL_INDUSTRY_RATE = "FCDNLR30";
  private static final String NO_AEM_CHANNEL = "No AEM channel for: %s";
  private static final String NO_AEM_SUBCHANNNEL = "No AEM subchannel for: %s";
  private static final String SUBCHANNEL_APPS = "apps";
  private static final String SUBCHANNEL_WEB = "web";

  public static String getLabelsEndpoint(CategoryEnumDto category, AemProperties aemProperties) {
    return switch (category) {
      case MAIN -> aemProperties.getLabelsEndpoint();
      case PI_BOOKINGS -> aemProperties.getAmendLabelsEndpoint();
      case BOOKING -> aemProperties.getBookingLabelsEndpoint();
      case PI_PRE_CHECKIN -> aemProperties.getPiPreCheckInEndpoint();
      case PI_GROUP_BOOKING -> aemProperties.getPiGroupBookingEndpoint();
      case EXTRAS -> aemProperties.getExtrasLabelsEndpoint();
      case PROMOTIONS -> aemProperties.getPromoLabelsEndpoint();
    };
  }

  public static String getGlobalConfigEndpoint(String channel, AemProperties aemProperties) {
    return switch (channel.toUpperCase()) {
      // initially, employee website offers are only taken from the leisure AEM endpoint
      case CHANNEL_PI, CHANNEL_EMPLOYEE, CHANNEL_TRAVEL_INDUSTRY_RATE ->
          aemProperties.getPiGlobalConfigEndpoint();
      case CHANNEL_BB -> aemProperties.getBbGlobalConfigEndpoint();
      case CHANNEL_CCUI -> aemProperties.getCcuiGlobalConfigEndpoint();
      case CHANNEL_DISTR -> aemProperties.getDistrGlobalConfigEndpoint();
      default -> throw new IllegalArgumentException(String.format(NO_AEM_CHANNEL, channel));
    };
  }

  public static String getPageDlpUriPath(String dlpPath, String country, String language,
      AemProperties aemProperties, AemDlpProperties aemDlpProperties) {

    return aemProperties.getPageDlpEndpoint()
        .replace(aemDlpProperties.getCountryParam(), country)
        .replace(aemDlpProperties.getLanguageParam(), language)
        .replace(aemDlpProperties.getDlpPathParam(), dlpPath);
  }

  public static String getAppsHomepageEndpoint(String channel, String subchannel, String country,
      String language,
      AemProperties aemProperties, AemHomepageProperties aemHomepageProperties) {

    var subchannelParam = subchannel.toLowerCase();
    if (!SUBCHANNEL_WEB.equals(subchannelParam) && !SUBCHANNEL_APPS.equals(subchannelParam)) {
      var exception = new ContentException(DIGITAL_APPS_HOMEPAGE_SUBCHANNEL_EXCEPTION,
          String.format(NO_AEM_SUBCHANNNEL, subchannel));
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    return switch (channel.toUpperCase()) {
      case CHANNEL_PI -> aemProperties.getAppsHomepageEndPoint()
          .replace(aemHomepageProperties.getCountryParam(), country)
          .replace(aemHomepageProperties.getLanguageParam(), language)
          .replace(aemHomepageProperties.getSubchannelParam(), subchannelParam)
          .replace(aemHomepageProperties.getPathParam(), "premier-inn");
      case CHANNEL_BB -> aemProperties.getAppsHomepageEndPoint()
          .replace(aemHomepageProperties.getCountryParam(), country)
          .replace(aemHomepageProperties.getLanguageParam(), language)
          .replace(aemHomepageProperties.getSubchannelParam(), subchannelParam)
          .replace(aemHomepageProperties.getPathParam(), "business-booker");
      default -> {
        var message = String.format(NO_AEM_CHANNEL, channel);
        var exception = new ContentException(DIGITAL_APPS_HOMEPAGE_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    };
  }
}
