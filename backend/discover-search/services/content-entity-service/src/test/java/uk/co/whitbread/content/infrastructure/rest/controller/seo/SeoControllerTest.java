package uk.co.whitbread.content.infrastructure.rest.controller.seo;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_SEO_UNSUPORTED_PAGE_EXCEPTION;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.index.header.data.out.Icon;
import uk.co.whitbread.content.domain.model.index.header.data.out.MsIcon;
import uk.co.whitbread.content.domain.model.seo.exception.UnsupportedPageException;
import uk.co.whitbread.content.domain.model.seo.in.SeoRequest;
import uk.co.whitbread.content.domain.model.seo.out.SeoResponse;
import uk.co.whitbread.content.domain.ports.primary.SeoInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.mapper.SeoRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.mapper.SeoResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.in.SeoRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out.IconDto;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out.MsIconDto;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out.SeoResponseDto;


@ExtendWith(MockitoExtension.class)
class SeoControllerTest {

  @InjectMocks
  private SeoController seoController;
  @Mock
  private SeoInPort seoInPort;
  @Mock
  private SeoRequestDtoMapper seoRequestDtoMapper;
  @Mock
  private SeoResponseDtoMapper seoResponseDtoMapper;

  @Test
  void getSeoInformation__ShouldReturnOK() {
    //Arrange
    var seoRequestDto = getSeoRequestDtoGbEn();
    var seoRequest = getSeoRequestGbEn();
    Mockito.when(seoRequestDtoMapper.toModel(seoRequestDto))
        .thenReturn(seoRequest);
    Mockito.when(seoInPort.getSeoInformation(seoRequest))
        .thenReturn(getSeoInformation());
    Mockito.when(seoResponseDtoMapper.toDto(getSeoInformation()))
        .thenReturn(getSeoInformationDto());

    //act
    var request = seoRequestDtoMapper.toModel(seoRequestDto);
    var seoDto = seoResponseDtoMapper.toDto(
        seoInPort.getSeoInformation(request));
    final ResponseEntity<SeoResponseDto> response = seoController.getSeo(seoRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), seoDto.getPageTitle(), response.getBody().getPageTitle());
    assertEquals(response.toString(), seoDto.getCardImageUrl(),
        response.getBody().getCardImageUrl());
    assertEquals(response.toString(), seoDto.getPageDescription(),
        response.getBody().getPageDescription());
    assertEquals(response.toString(), seoDto.getFaviconUrl(), response.getBody().getFaviconUrl());
    assertEquals(response.toString(), seoDto.getIcons(), response.getBody().getIcons());
    assertEquals(response.toString(), seoDto.getMsIcons(), response.getBody().getMsIcons());
  }

  @Test
  void getSeoInformation__ShouldReturnException() {
    //Arrange
    var exception = new UnsupportedPageException(DIGITAL_SEO_UNSUPORTED_PAGE_EXCEPTION, "message");
    var seoRequestDto = getSeoRequestDtoGbEn();
    Mockito.when(seoInPort.getSeoInformation(any()))
        .thenThrow(exception);

    //act
    var actual =
        assertThrows(UnsupportedPageException.class,
            () -> seoController.getSeo(seoRequestDto));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(DIGITAL_SEO_UNSUPORTED_PAGE_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("message"));
    assertThat(actual.getErrorCode(), is(DIGITAL_SEO_UNSUPORTED_PAGE_EXCEPTION.getCode()));
  }

  @Test
  void getSeoInformation__ShouldNotFindSeoInformation() {
    //Arrange
    var seoRequestDtoGbEn = getSeoRequestDtoGbEn();
    var seoRequestDtoDeDe = getSeoRequestDtoDeDe();
    var seoRequest = getSeoRequestGbEn();
    Mockito.when(seoRequestDtoMapper.toModel(seoRequestDtoGbEn))
        .thenReturn(seoRequest);
    Mockito.when(seoInPort.getSeoInformation(seoRequest))
        .thenReturn(getSeoInformation());
    Mockito.when(seoResponseDtoMapper.toDto(getSeoInformation()))
        .thenReturn(getSeoInformationDto());

    //act
    var request = seoRequestDtoMapper.toModel(seoRequestDtoGbEn);
    var seoInformationDto = seoResponseDtoMapper.toDto(
        seoInPort.getSeoInformation(request));
    final ResponseEntity<SeoResponseDto> response = seoController.getSeo(seoRequestDtoDeDe);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertNull(response.getBody());
  }


  private SeoResponse getSeoInformation() {
    return SeoResponse.builder()
        .pageTitle("Manchester Old Trafford Hotel | Premier Inn")
        .pageDescription("Manchester Old Trafford Hotel Description")
        .cardImageUrl(
            "https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg")
        .faviconUrl(
            "https://www.premierinn.com/content/dam/pi/websites/desktop/icons/favicons/favicon.ico")
        .icons(of(Icon.builder()
            .rel("icon")
            .sizes("228x228")
            .href(
                "https://www.premierinn.com/content/dam/pi/websites/desktop/icons/favicons/xfavicon-228x228.png.pagespeed.ic.AhL0MMwPlZ.webp")
            .build()))
        .msIcons(of(MsIcon.builder()
            .name("msapplication-TileImage")
            .content(
                "https://www.premierinn.com/content/dam/pi/websites/desktop/icons/favicons/favicon-ie10-144x144.png")
            .build()))
        .build();
  }

  private SeoResponseDto getSeoInformationDto() {
    return SeoResponseDto.builder()
        .pageTitle("Manchester Old Trafford Hotel | Premier Inn")
        .pageDescription("Manchester Old Trafford Hotel Description")
        .cardImageUrl(
            "https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg")
        .faviconUrl(
            "https://www.premierinn.com/content/dam/pi/websites/desktop/icons/favicons/favicon.ico")
        .icons(of(IconDto.builder()
            .rel("icon")
            .sizes("228x228")
            .href(
                "https://www.premierinn.com/content/dam/pi/websites/desktop/icons/favicons/xfavicon-228x228.png.pagespeed.ic.AhL0MMwPlZ.webp")
            .build()))
        .msIcons(of(MsIconDto.builder()
            .name("msapplication-TileImage")
            .content(
                "https://www.premierinn.com/content/dam/pi/websites/desktop/icons/favicons/favicon-ie10-144x144.png")
            .build()))
        .build();
  }

  private SeoRequestDto getSeoRequestDtoGbEn() {
    return SeoRequestDto.builder()
        .hotelId("MANOLD")
        .page("HDP")
        .country("gb")
        .language("en")
        .build();
  }

  private SeoRequest getSeoRequestGbEn() {
    return SeoRequest.builder()
        .hotelId("MANOLD")
        .page("HDP")
        .country("gb")
        .language("en")
        .build();
  }

  private SeoRequestDto getSeoRequestDtoDeDe() {
    return SeoRequestDto.builder()
        .hotelId("MANOLD")
        .page("HDP")
        .country("de")
        .language("de")
        .build();
  }
}


