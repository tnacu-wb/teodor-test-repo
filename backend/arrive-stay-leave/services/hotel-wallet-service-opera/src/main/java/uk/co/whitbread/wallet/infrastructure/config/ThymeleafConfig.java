package uk.co.whitbread.wallet.infrastructure.config;

import java.util.Collection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class ThymeleafConfig {

  /* Constant(s): */
  /* Parameter keys for all the message templates. */
  public static final String TEMPLATE_RESERVATIONID_PARAM = "reservationId";
  public static final String TEMPLATE_ARRIVAL_DATE_PARAM = "arrivalDate";
  public static final String TEMPLATE_ARRIVAL_DATE_ISO8601_PARAM = "arrivalDateISO8601";
  public static final String TEMPLATE_BOOKER_WITH_TITLE_PARAM = "bookerWithTitle";
  public static final String TEMPLATE_HOTEL_NAME_PARAM = "hotelName";
  public static final String TEMPLATE_HOTEL_BRAND_PARAM = "hotelBrand";
  public static final String TEMPLATE_HOTEL_LATITUDE_PARAM = "hotelLatitude";
  public static final String TEMPLATE_HOTEL_LONGITUDE_PARAM = "hotelLongitude";
  public static final String TEMPLATE_ADDRESS_PARAM = "address";
  public static final String TEMPLATE_PHONE_PARAM = "phone";
  public static final String TEMPLATE_CHECK_IN_TIME_PARAM = "checkInTime";
  public static final String TEMPLATE_CHECK_OUT_TIME_PARAM = "checkOutTime";
  public static final String TEMPLATE_PARKING_PARAM = "parking";
  public static final String TEMPLATE_LINKS_PARAM = "links";
  public static final String TEMPLATE_CONTACT_CENTRE_PARAM = "contactCentre";
  public static final String TEMPLATE_FOREGROUND_COLOR_PARAM = "foregroundColor";
  public static final String TEMPLATE_BACKGROUND_COLOR_PARAM = "backgroundColor";
  public static final String TEMPLATE_LABEL_COLOR_PARAM = "labelColor";

  /**
   * Creates the template resolver that retrieves JSON message payloads.
   *
   * @return Template resolver.
   */
  @Bean
  public ITemplateResolver jsonMessageTemplateResolver() {
    StringTemplateResolver theResourceTemplateResolver =
        new StringTemplateResolver();

    theResourceTemplateResolver.setTemplateMode("text");
    theResourceTemplateResolver.setCacheable(false);
    return theResourceTemplateResolver;
  }

  /**
   * Creates the template engine for all message templates.
   *
   * @param inTemplateResolvers Template resolver for different types of messages etc. Note that any
   *                            template resolvers defined elsewhere will also be included in this
   *                            collection.
   * @return Template engine.
   */
  @Bean
  public TemplateEngine templateEngine(
      final Collection<SpringResourceTemplateResolver> inTemplateResolvers) {
    final SpringTemplateEngine theTemplateEngine = new SpringTemplateEngine();
    for (SpringResourceTemplateResolver theTemplateResolver : inTemplateResolvers) {
      theTemplateEngine.addTemplateResolver(theTemplateResolver);
    }
    theTemplateEngine.addTemplateResolver(jsonMessageTemplateResolver());
    return theTemplateEngine;
  }
}