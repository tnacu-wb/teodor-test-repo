package uk.co.whitbread.wallet.domain.logic;

import static uk.co.whitbread.wallet.domain.utils.PassDataMetricsUtils.incrementMandatoryPassDataMissing;
import static uk.co.whitbread.wallet.domain.utils.TemplateUtils.getTime;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_ADDRESS_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_ARRIVAL_DATE_ISO8601_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_ARRIVAL_DATE_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_BACKGROUND_COLOR_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_BOOKER_WITH_TITLE_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_CHECK_IN_TIME_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_CHECK_OUT_TIME_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_CONTACT_CENTRE_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_FOREGROUND_COLOR_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_HOTEL_BRAND_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_HOTEL_LATITUDE_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_HOTEL_LONGITUDE_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_HOTEL_NAME_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_LABEL_COLOR_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_LINKS_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_PARKING_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_PHONE_PARAM;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_RESERVATIONID_PARAM;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.signing.IPKSigningUtil;
import de.brendamour.jpasskit.signing.PKSigningException;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import de.brendamour.jpasskit.signing.PKSigningInformationUtil;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.wallet.ErrorCode;
import uk.co.whitbread.wallet.domain.exception.PassJsonCreationException;
import uk.co.whitbread.wallet.domain.exception.SignatureException;
import uk.co.whitbread.wallet.domain.exception.WalletCreationException;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.model.out.HotelInfo;
import uk.co.whitbread.wallet.domain.model.out.ReservationDetails;
import uk.co.whitbread.wallet.domain.ports.primary.WalletGeneratorInPort;
import uk.co.whitbread.wallet.domain.ports.secondary.CertsRetrieverOutPort;
import uk.co.whitbread.wallet.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.wallet.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;
import uk.co.whitbread.wallet.domain.utils.LocalFileRetriever;
import uk.co.whitbread.wallet.domain.utils.TemplateUtils;
import uk.co.whitbread.wallet.domain.utils.passkit.Type;
import uk.co.whitbread.wallet.domain.utils.passkit.TypeFactory;

@Slf4j
@RequiredArgsConstructor
public class WalletGeneratorInPortImpl implements WalletGeneratorInPort {

  private static final String GER = "Germany";
  private static final String DE = "Deutschland";
  private final WalletProperties walletProperties;
  private final ObjectMapper objectMapper;
  private final IPKSigningUtil ipkSigningUtil;
  private final TemplateEngine templateEngine;
  private final HotelReservationOutPort hotelReservationOutPort;
  private final ContentOutPort contentOutPort;
  private final PKSigningInformationUtil pkSigningInformationUtil;
  private final CertsRetrieverOutPort certsRetrieverOutPort;

  public byte[] generateWalletPass(WalletRequest walletRequest) {
    String basketReference = hotelReservationOutPort.findBooking(walletRequest)
        .getBasketReference();
    ReservationDetails reservationDetails = hotelReservationOutPort.getReservationDetails(
        basketReference, walletProperties.getEarlyCheckInTime());
    HotelInfo hotelInfo = contentOutPort.getHotelInformation(walletRequest.getCountry(),
        walletRequest.getLanguage(), reservationDetails.getHotelId());
    InputStream passTemplate = LocalFileRetriever.getFileFromClasspath(
        walletProperties.getPassJson());

    // Validate if all the mandatory data for pass creation is available for the Canary Deployment.
    validatePassData(reservationDetails, hotelInfo);
    Type type = TypeFactory.create(walletProperties, walletRequest.getChannel(),
        hotelInfo.getBrand(), GER.equals(hotelInfo.getCountry()) || DE.equals(hotelInfo.getCountry()));

    String jsonPass = fillInPassDetails(LocalFileRetriever.getStringFromInputStream(passTemplate),
        reservationDetails, hotelInfo, type);

    return generateSignedPass(jsonPass, type.getName(), walletRequest.isExcludeBarcode());
  }

  /**
   * Create a .pkpass file
   *
   * @param passString     the template
   * @param type           is used to find the specific folder with images. Values: PI, BB, Hub,
   *                       Zip
   * @param excludeBarcode exclude barcode flag
   * @return byte array representing the zipped archive of apple passkit
   */
  private byte[] generateSignedPass(String passString, String type, boolean excludeBarcode) {
    PKPass pass;
    PKSigningInformation pkSigningInformation;
    try {
      pass = objectMapper.readValue(passString, PKPass.class);
      if (excludeBarcode) {
        pass.getBarcodes().clear();
      }
    } catch (IOException e) {
      var ex = new PassJsonCreationException(ErrorCode.DIGITAL_PASS_JSON_CREATION_EXCEPTION,
          "JSON pass couldn't be serialized", e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    InputStream p12 = certsRetrieverOutPort.getP12();
    InputStream appleWWDRCA = certsRetrieverOutPort.getAppleWwdrca();

    try {
      pkSigningInformation = pkSigningInformationUtil.loadSigningInformationFromPKCS12AndIntermediateCertificate(
          p12, walletProperties.getCert().getPassword(), appleWWDRCA);
    } catch (Exception e) {
      var ex = new SignatureException(ErrorCode.DIGITAL_WALLET_SIGNATURE_EXCEPTION,
          "Unable to load signing information", e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    try {
      return ipkSigningUtil.createSignedAndZippedPkPassArchive(pass,
          TemplateUtils.getPkPassTemplate(walletProperties.getTemplateDir(), type),
          pkSigningInformation);
    } catch (PKSigningException e) {
      var ex = new WalletCreationException(ErrorCode.DIGITAL_WALLET_CREATION_EXCEPTION,
          "Unable to create signed wallet pass", e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  /**
   * This method validates if all the required pass data is available for creating a pass. If any of
   * the required data for creating a pass is null then it increments a counter which tracks the
   * number of times mandatory data for pass creation is missing.
   *
   * @param reservationDetails contains the reservations details like last name, confirmation
   *                           number, check-in time, check-out time, arrival date.
   * @param hotelInfo          contains the information of hotel like hotel name, brand, latitude,
   *                           longitude.
   */
  public static void validatePassData(ReservationDetails reservationDetails, HotelInfo hotelInfo) {
    if (StringUtils.isAnyBlank(hotelInfo.getHotelName(), reservationDetails.getLastName(),
        reservationDetails.getConfirmationNumber(), hotelInfo.getBrand(),
        reservationDetails.getCheckInTime(), reservationDetails.getCheckOutTime())
        || Objects.isNull(reservationDetails.getArrivalDate())
        || Objects.isNull(hotelInfo.getLatitude())
        || Objects.isNull(hotelInfo.getLongitude())) {
      incrementMandatoryPassDataMissing();
    }
  }

  /**
   * Fills the template.
   *
   * @param pass               the json template
   * @param reservationDetails the details from the reservation
   * @param hotelInfo          the info about the hotel
   * @param type               contains the changes for each template
   * @return JSON message with inserted parameter values
   */
  private String fillInPassDetails(String pass, ReservationDetails reservationDetails,
      HotelInfo hotelInfo, Type type) {
    final Context theContext = new Context();
    theContext.setVariable(TEMPLATE_HOTEL_NAME_PARAM, hotelInfo.getHotelName());
    theContext.setVariable(TEMPLATE_ARRIVAL_DATE_ISO8601_PARAM,
        String.format("%sT%s:00Z", reservationDetails.getArrivalDate().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd")), reservationDetails.getCheckInTime()));
    theContext.setVariable(TEMPLATE_ARRIVAL_DATE_PARAM,
        reservationDetails.getArrivalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    theContext.setVariable(TEMPLATE_BOOKER_WITH_TITLE_PARAM,
        String.format("%s %s", reservationDetails.getTitle(), reservationDetails.getLastName()));
    theContext.setVariable(TEMPLATE_HOTEL_BRAND_PARAM, hotelInfo.getBrand());
    theContext.setVariable(TEMPLATE_HOTEL_LATITUDE_PARAM, hotelInfo.getLatitude());
    theContext.setVariable(TEMPLATE_HOTEL_LONGITUDE_PARAM, hotelInfo.getLongitude());
    theContext.setVariable(TEMPLATE_RESERVATIONID_PARAM,
        reservationDetails.getConfirmationNumber());
    theContext.setVariable(TEMPLATE_ADDRESS_PARAM, hotelInfo.getAddress());
    theContext.setVariable(TEMPLATE_PHONE_PARAM, hotelInfo.getPhone());
    theContext.setVariable(TEMPLATE_CHECK_IN_TIME_PARAM,
        getTime(reservationDetails.getArrivalDate(), reservationDetails.getCheckInTime()));
    theContext.setVariable(TEMPLATE_CHECK_OUT_TIME_PARAM,
        getTime(reservationDetails.getDepartureDate(),
            reservationDetails.getCheckOutTime()));
    theContext.setVariable(TEMPLATE_PARKING_PARAM, hotelInfo.getParking());
    theContext.setVariable(TEMPLATE_LINKS_PARAM, hotelInfo.getLinks());
    theContext.setVariable(TEMPLATE_CONTACT_CENTRE_PARAM, reservationDetails.getContactCentre());
    theContext.setVariable(TEMPLATE_BACKGROUND_COLOR_PARAM, type.getBackgroundColor());
    theContext.setVariable(TEMPLATE_FOREGROUND_COLOR_PARAM, type.getForegroundColor());
    theContext.setVariable(TEMPLATE_LABEL_COLOR_PARAM, type.getLabelColor());
    final String theJsonMessage =
        templateEngine.process(pass, theContext);
    log.debug("The pass is: {}", theJsonMessage);
    if (theJsonMessage != null) {
      return theJsonMessage.replace("\n", "");
    }
    return pass;
  }
}
