package uk.co.whitbread.content.infrastructure.rest.controller.footer;

import static java.util.List.of;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;
import uk.co.whitbread.content.domain.model.footer.out.LinkColumns;
import uk.co.whitbread.content.domain.model.footer.out.LinkItems;
import uk.co.whitbread.content.domain.model.footer.out.LinkTabs;
import uk.co.whitbread.content.domain.model.footer.out.NewsletterSignup;
import uk.co.whitbread.content.domain.ports.primary.FooterInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.mapper.FooterRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.mapper.FooterResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.in.FooterRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out.FooterResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out.LinkColumnsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out.LinkItemsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out.LinkTabsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out.NewsletterSignupDto;


@ExtendWith(MockitoExtension.class)
class FooterControllerTest {

  @InjectMocks
  private FooterController footerController;
  @Mock
  private FooterInPort footerInPort;
  @Mock
  private FooterRequestDtoMapper footerRequestDtoMapper;
  @Mock
  private FooterResponseDtoMapper footerResponseDtoMapper;

  @Test
  void getFooterInformation__ShouldReturnOK() {
    //Arrange
    var footerRequestDto = getFooterRequestDtoGbEn();
    var footerRequest = getFooterRequestGbEn();
    Mockito.when(footerRequestDtoMapper.toModel(footerRequestDto))
        .thenReturn(footerRequest);
    Mockito.when(footerInPort.getFooterInformation(footerRequest))
        .thenReturn(getFooterInformation());
    Mockito.when(footerResponseDtoMapper.toDto(getFooterInformation()))
        .thenReturn(getFooterInformationDto());

    //act
    var request = footerRequestDtoMapper.toModel(footerRequestDto);
    var footerDto = footerResponseDtoMapper.toDto(
        footerInPort.getFooterInformation(request));
    final ResponseEntity<FooterResponseDto> response = footerController.getFooter(footerRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), footerDto.getCopyrightInfo(), response.getBody().getCopyrightInfo());
    assertEquals(response.toString(), footerDto.getTabs(), response.getBody().getTabs());
    assertEquals(response.toString(), footerDto.getNewsletterSignup(), response.getBody().getNewsletterSignup());

  }

  @Test
  void getFooterInformation__ShouldNotFindFooter() {
    //Arrange
    var footerRequestDtoGbEn = getFooterRequestDtoGbEn();
    var footerRequestDtoDeDe = getFooterRequestDtoDeDe();
    var footerRequest = getFooterRequestGbEn();
    Mockito.when(footerRequestDtoMapper.toModel(footerRequestDtoGbEn))
        .thenReturn(footerRequest);
    Mockito.when(footerInPort.getFooterInformation(footerRequest))
        .thenReturn(getFooterInformation());
    Mockito.when(footerResponseDtoMapper.toDto(getFooterInformation()))
        .thenReturn(getFooterInformationDto());

    //act
    var request = footerRequestDtoMapper.toModel(footerRequestDtoGbEn);
    var footerInformationDto = footerResponseDtoMapper.toDto(
        footerInPort.getFooterInformation(request));
    final ResponseEntity<FooterResponseDto> response = footerController.getFooter(footerRequestDtoDeDe);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertNull(response.getBody());
  }

  private FooterResponse getFooterInformation() {
    return FooterResponse.builder()
        .copyrightInfo("&copy; 2022 Premier Inn")
        .newsletterSignup(NewsletterSignup.builder()
                .bookStayButtonText("Back to Homepage")
                .bookStayButtonUrl("/")
                .confirmationText("Great news – you’re now part of our Premier Inn mailing list, so keep an eye on your inbox!")
                .build())
        .tabs(of(LinkTabs.builder()
                .name("City breaks")
                .columns(of(LinkColumns.builder()
                        .name("")
                        .linkItems(of(LinkItems.builder()
                                .linkSrc("http://www.uat3.premierinn.digital/gb/en/hotels/england/greater-london/london/kensington.html")
                                .name("London - Kensington hotels")
                                .openInNewTab(false)
                                .build()))
                        .build()))
                .build()))
        .build();
  }


  private FooterResponseDto getFooterInformationDto() {
    return FooterResponseDto.builder()
            .copyrightInfo("&copy; 2022 Premier Inn")
            .newsletterSignup(NewsletterSignupDto.builder()
                    .bookStayButtonText("Back to Homepage")
                    .bookStayButtonUrl("/")
                    .confirmationText("Great news – you’re now part of our Premier Inn mailing list, so keep an eye on your inbox!")
                    .build())
            .bottomLinks(of(LinkItemsDto.builder()
                    .linkSrc("/gb/en/b.html")
                    .name("Privacy")
                    .openInNewTab(false)
                    .build()))
            .tabs(of(LinkTabsDto.builder()
                    .name("City breaks")
                    .columns(of(LinkColumnsDto.builder()
                            .name("")
                            .linkItems(of(LinkItemsDto.builder()
                                    .linkSrc("http://www.uat3.premierinn.digital/gb/en/hotels/england/greater-london/london/kensington.html")
                                    .name("London - Kensington hotels")
                                    .openInNewTab(false)
                                    .build()))
                            .build()))
                    .build()))
            .build();
  }

  private FooterRequestDto getFooterRequestDtoGbEn() {
    return FooterRequestDto.builder()
        .country("gb")
        .language("en")
        .site("leisure")
        .build();
  }

  private FooterRequestDto getFooterRequestDtoDeDe() {
    return FooterRequestDto.builder()
        .country("de")
        .language("de")
        .site("leisure")
        .build();
  }

  private FooterRequest getFooterRequestGbEn() {
    return FooterRequest.builder()
        .country("gb")
        .language("en")
        .site("leisure")
        .build();
  }

}


