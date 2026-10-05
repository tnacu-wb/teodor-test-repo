package uk.co.whitbread.wallet.domain.logic;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.wallet.infrastructure.config.ThymeleafConfig.TEMPLATE_ARRIVAL_DATE_ISO8601_PARAM;
import static uk.co.whitbread.wallet.utils.TestUtils.bb;
import static uk.co.whitbread.wallet.utils.TestUtils.hub;
import static uk.co.whitbread.wallet.utils.TestUtils.pi;
import static uk.co.whitbread.wallet.utils.TestUtils.zip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.signing.IPKPassTemplate;
import de.brendamour.jpasskit.signing.IPKSigningUtil;
import de.brendamour.jpasskit.signing.PKSigningException;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import de.brendamour.jpasskit.signing.PKSigningInformationUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.wallet.ErrorCode;
import uk.co.whitbread.wallet.domain.exception.PassJsonCreationException;
import uk.co.whitbread.wallet.domain.exception.SignatureException;
import uk.co.whitbread.wallet.domain.exception.WalletCreationException;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.model.out.HotelInfo;
import uk.co.whitbread.wallet.domain.model.out.ReservationDetails;
import uk.co.whitbread.wallet.domain.ports.secondary.CertsRetrieverOutPort;
import uk.co.whitbread.wallet.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.wallet.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;
import uk.co.whitbread.wallet.domain.utils.LocalFileRetriever;

@ExtendWith(MockitoExtension.class)
class WalletGeneratorInPortImplTest {

  @InjectMocks
  private WalletGeneratorInPortImpl walletGeneratorInPort;

  @Mock
  private HotelReservationOutPort hotelReservationOutPort;
  @Mock
  private ContentOutPort contentOutPort;
  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private IPKSigningUtil ipkSigningUtil;
  @Mock
  private TemplateEngine templateEngine;
  @Mock
  private PKSigningInformationUtil pkSigningInformationUtil;
  @Mock
  private CertsRetrieverOutPort certsRetrieverOutPort;
  @Mock
  WalletProperties.Cert cert;
  @Captor
  ArgumentCaptor<Context> contextCaptor;

  @Spy
  private WalletProperties walletProperties;

  private byte[] walletPassBytes;

  @BeforeEach
  void setup() {
    initWalletProperties();

    walletPassBytes = new byte[]{21, 121, 101, 45, 62, 118, 101, 114, 61, 101, 98};

    var findBookingResponseDto = new FindBookingResponseDto();
    findBookingResponseDto.setBasketReference("reference");
    when(hotelReservationOutPort.findBooking(any())).thenReturn(findBookingResponseDto);
    when(hotelReservationOutPort.getReservationDetails(any(), anyString())).thenReturn(getReservationDetails());
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(getHotelInfo());

  }

  @Test
  void generateWalletTest_success() throws IOException, PKSigningException, CertificateException {

    when(templateEngine.process(anyString(), any())).thenReturn("json pass");
    PKPass pkPass = mock(PKPass.class);
    when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(pkPass);
    mockCert();
    when(pkSigningInformationUtil.loadSigningInformationFromPKCS12AndIntermediateCertificate(
        any(InputStream.class), anyString(), any(InputStream.class))).thenReturn(
        new PKSigningInformation());
    when(ipkSigningUtil.createSignedAndZippedPkPassArchive(any(PKPass.class),
        any(IPKPassTemplate.class), any(PKSigningInformation.class))).thenReturn(walletPassBytes);
    when(certsRetrieverOutPort.getP12()).thenReturn(
        new ByteArrayInputStream(new byte[]{21, 121, 101}));
    when(certsRetrieverOutPort.getAppleWwdrca()).thenReturn(
        new ByteArrayInputStream(new byte[]{61, 101, 98}));

    // Act
    var result = walletGeneratorInPort.generateWalletPass(
        new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", true));

    // Assert
    assertThat(result, is(walletPassBytes));
    verify(pkPass).getBarcodes();
  }

  @Test
  void generateWalletTestCheckInTime_success()
      throws IOException, PKSigningException, CertificateException {

    ReservationDetails reservation = ReservationDetails.builder()
        .hotelId("test")
        .checkInTime("11:00:00")
        .checkOutTime("20:00:00")
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now())
        .firstName("aaa")
        .lastName("bbb")
        .hotelId("hotel")
        .confirmationNumber("confirm")
        .build();
    var expectedTime = String.format("%sT%s:00Z", reservation.getArrivalDate().format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd")), reservation.getCheckInTime());

    InputStream passTemplate = LocalFileRetriever.getFileFromClasspath(
        walletProperties.getPassJson());
    when(hotelReservationOutPort.findBooking(any())).thenReturn(mock(FindBookingResponseDto.class));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfo.builder()
            .brand("PI")
            .hotelName("hotelname")
            .latitude(Double.valueOf(100))
            .longitude(Double.valueOf(100))
            .build());
    when(hotelReservationOutPort.getReservationDetails(any(), anyString())).thenReturn(reservation);
    when(templateEngine.process(eq(LocalFileRetriever.getStringFromInputStream(passTemplate)),
        contextCaptor.capture())).thenCallRealMethod();
    PKPass pkPass = mock(PKPass.class);
    when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(pkPass);
    mockCert();
    when(pkSigningInformationUtil.loadSigningInformationFromPKCS12AndIntermediateCertificate(
        any(InputStream.class), anyString(), any(InputStream.class))).thenReturn(
        new PKSigningInformation());
    when(ipkSigningUtil.createSignedAndZippedPkPassArchive(any(PKPass.class),
        any(IPKPassTemplate.class), any(PKSigningInformation.class))).thenReturn(walletPassBytes);
    when(certsRetrieverOutPort.getP12()).thenReturn(
        new ByteArrayInputStream(new byte[]{21, 121, 101}));
    when(certsRetrieverOutPort.getAppleWwdrca()).thenReturn(
        new ByteArrayInputStream(new byte[]{61, 101, 98}));

    // Act
    walletGeneratorInPort.generateWalletPass(
        new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", true));

    // Assert
    Context value = contextCaptor.getValue();
    assertEquals(expectedTime, value.getVariable(TEMPLATE_ARRIVAL_DATE_ISO8601_PARAM));
  }

  @Test
  void generateWalletTest_canary_error() {
    when(hotelReservationOutPort.getReservationDetails(any(), anyString())).thenReturn(new ReservationDetails());
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(new HotelInfo());

    // Act
    Exception exception = assertThrows(Exception.class, () -> {
      walletGeneratorInPort.generateWalletPass(
          new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", true));
    });
    // Assert
    assertNotNull(exception);
  }

  @Test
  void generateWallet_whenTemplateDirNotFound_throwsException()
      throws IOException, CertificateException {

    when(templateEngine.process(anyString(), any())).thenReturn("json pass");
    when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(
        PKPass.builder().build());
    mockCert();
    when(pkSigningInformationUtil.loadSigningInformationFromPKCS12AndIntermediateCertificate(
        any(InputStream.class), anyString(), any(InputStream.class))).thenReturn(
        new PKSigningInformation());
    when(certsRetrieverOutPort.getP12()).thenReturn(
        new ByteArrayInputStream(new byte[]{21, 121, 101}));
    when(certsRetrieverOutPort.getAppleWwdrca()).thenReturn(
        new ByteArrayInputStream(new byte[]{61, 101, 98}));
    walletProperties.setTemplateDir("tmp");

    // Act
    WalletCreationException walletCreationException = assertThrowsExactly(
        WalletCreationException.class,
        () -> walletGeneratorInPort.generateWalletPass(new WalletRequest()));

    // Assert
    assertThat(walletCreationException.getErrorCode(),
        is(ErrorCode.DIGITAL_BAD_TEMPLATE_CONFIGURATION_EXCEPTION.getCode()));
  }

  @Test
  void generateWallet_whenTemplatePassNotFound_throwsException() {
    walletProperties.setPassJson("alt-pass.json");

    // Act
    PassJsonCreationException passJsonCreationException = assertThrowsExactly(
        PassJsonCreationException.class,
        () -> walletGeneratorInPort.generateWalletPass(new WalletRequest()));

    // Assert
    assertThat(passJsonCreationException.getErrorCode(),
        is(ErrorCode.DIGITAL_COULD_NOT_FIND_FILE_EXCEPTION.getCode()));
  }

  @Test
  void generateWallet_whenIOUtilsToString_throwsException() {
    try (MockedStatic<IOUtils> ioUtils = mockStatic(IOUtils.class)) {
      ioUtils.when(() -> IOUtils.toString(any(InputStream.class), any(Charset.class)))
          .thenThrow(new IOException("IOException"));

      // Act
      PassJsonCreationException passJsonCreationException = assertThrowsExactly(
          PassJsonCreationException.class,
          () -> walletGeneratorInPort.generateWalletPass(new WalletRequest()));

      // Assert
      assertThat(passJsonCreationException.getErrorCode(),
          is(ErrorCode.DIGITAL_COULD_NOT_READ_PASS_INPUT_STREAM_EXCEPTION.getCode()));
    }
  }

  @Test
  void generateWallet_whenObjectMapperThrowsException() throws JsonProcessingException {
    when(templateEngine.process(anyString(), any())).thenReturn("json pass");
    when(objectMapper.readValue(anyString(), any(Class.class))).thenThrow(
        new JsonProcessingException("IOException") {
        });

    // Act
    PassJsonCreationException passJsonCreationException = assertThrowsExactly(
        PassJsonCreationException.class,
        () -> walletGeneratorInPort.generateWalletPass(new WalletRequest()));

    // Assert
    assertThat(passJsonCreationException.getErrorCode(),
        is(ErrorCode.DIGITAL_PASS_JSON_CREATION_EXCEPTION.getCode()));
  }

  @Test
  void generateWallet_whenLoadingCertificateInfoFails_throwsException()
      throws IOException, CertificateException {

    when(templateEngine.process(anyString(), any())).thenReturn("json pass");
    PKPass pkPassMock = mock(PKPass.class);
    when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(pkPassMock);
    mockCert();
    when(pkSigningInformationUtil.loadSigningInformationFromPKCS12AndIntermediateCertificate(
        any(InputStream.class), anyString(), any(InputStream.class))).thenThrow(
        new CertificateException());

    // Act
    SignatureException signatureException = assertThrowsExactly(SignatureException.class,
        () -> walletGeneratorInPort.generateWalletPass(
            new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", false)));

    // Assert
    assertFalse(signatureException.getMessage().isEmpty());
    assertNotEquals(0, signatureException.getErrorCode());
    assertFalse(ErrorCode.DIGITAL_WALLET_SIGNATURE_EXCEPTION.getMessage().isEmpty());
    assertNotNull(ErrorCode.DIGITAL_WALLET_SIGNATURE_EXCEPTION.getMessage());
    assertThat(signatureException.getErrorCode(),
        is(ErrorCode.DIGITAL_WALLET_SIGNATURE_EXCEPTION.getCode()));
    verify(pkPassMock, times(0)).getBarcodes();
  }

  @Test
  void generateWallet_whenPKPassSigning_throwsException()
      throws IOException, CertificateException, PKSigningException {

    when(templateEngine.process(anyString(), any())).thenReturn("json pass");
    when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(
        PKPass.builder().build());
    mockCert();
    when(pkSigningInformationUtil.loadSigningInformationFromPKCS12AndIntermediateCertificate(
        any(InputStream.class), any(), any(InputStream.class))).thenReturn(
        new PKSigningInformation());
    when(ipkSigningUtil.createSignedAndZippedPkPassArchive(any(PKPass.class),
        any(IPKPassTemplate.class), any(PKSigningInformation.class))).thenThrow(
        new PKSigningException("Signing exception", new Exception()));
    when(certsRetrieverOutPort.getP12()).thenReturn(
        new ByteArrayInputStream(new byte[]{21, 121, 101}));
    when(certsRetrieverOutPort.getAppleWwdrca()).thenReturn(
        new ByteArrayInputStream(new byte[]{61, 101, 98}));

    // Act
    WalletCreationException walletCreationException = assertThrowsExactly(
        WalletCreationException.class,
        () -> walletGeneratorInPort.generateWalletPass(new WalletRequest()));

    // Assert
    assertThat(walletCreationException.getErrorCode(),
        is(ErrorCode.DIGITAL_WALLET_CREATION_EXCEPTION.getCode()));
  }

  private void mockCert() {
    when(walletProperties.getCert()).thenReturn(cert);
    when(cert.getPassword()).thenReturn("eA12dsa23dummy");
  }

  private void initWalletProperties() {
    walletProperties.setPassJson("pass.json");
    walletProperties.setTemplateDir("template");
    walletProperties.setPi(pi());
    walletProperties.setHub(hub());
    walletProperties.setZip(zip());
    walletProperties.setBb(bb());
    walletProperties.setEarlyCheckInTime("11:00");
  }

  private static ReservationDetails getReservationDetails() {
    return ReservationDetails.builder().hotelId("FRAMTI").lastName("Herbert").firstName("Frank")
        .checkInTime("15:00:00").checkOutTime("12:00:00").arrivalDate(LocalDate.parse("2024-12-27"))
        .departureDate(LocalDate.parse("2024-12-29")).confirmationNumber("NUM").build();
  }

  private static HotelInfo getHotelInfo() {
    return HotelInfo.builder().hotelId("FRAMTI").hotelName("Frankfurt").brand("PI")
        .phone("07778888888").address("29 Wurdstrasse").latitude(12.0).longitude(13.0).build();
  }
}
