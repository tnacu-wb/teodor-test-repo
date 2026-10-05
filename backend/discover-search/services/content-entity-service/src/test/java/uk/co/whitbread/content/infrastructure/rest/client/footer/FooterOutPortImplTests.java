package uk.co.whitbread.content.infrastructure.rest.client.footer;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_FOOTER_INFO_EXCEPTION;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;
import uk.co.whitbread.content.domain.model.footer.out.LinkColumns;
import uk.co.whitbread.content.domain.model.footer.out.LinkItems;
import uk.co.whitbread.content.domain.model.footer.out.LinkTabs;
import uk.co.whitbread.content.domain.model.footer.out.NewsletterSignup;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.footer.adapter.FooterAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.footer.mapper.FooterRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.footer.mapper.FooterResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.FooterResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkColumnsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkItemsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkTabsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.NewsletterSignupAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.out.FooterRequestAemDto;

@ExtendWith(MockitoExtension.class)
class FooterOutPortImplTests {

  @InjectMocks
  private FooterOutPortImpl footerOutPort;

  @Mock
  private FooterAemClient aemClient;

  @Mock
  private FooterResponseMapper footerResponseMapper;

  @Mock
  private FooterRequestMapper footerRequestMapper;

  Exception exception = new Exception();

  @Test
  void getFooter_badInput_ShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get footer information.";
    when(footerRequestMapper.toDto(any())).thenReturn(new FooterRequestAemDto());
    when(aemClient.getFooterInformation(any())).thenThrow(
        new AemResponseException(AEM_FOOTER_INFO_EXCEPTION, expectedMessage, exception));
    var request = new FooterRequest("null", "null", "null");
    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> footerOutPort.getFooterInformation(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getDebugMessage(), is(expectedMessage));
  }

  @Test
  void getFooter_ShouldReturnResourceNotFound() {
    //Arrange
    var expectedMessage = "Unable to get footer information.";
    when(footerRequestMapper.toDto(any())).thenReturn(new FooterRequestAemDto());
    when(aemClient.getFooterInformation(any())).thenThrow(
        new AemResponseException(AEM_FOOTER_INFO_EXCEPTION, expectedMessage, exception));
    var request = new FooterRequest("null", "null", "null");

    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        footerOutPort.getFooterInformation(request)
    );

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_FOOTER_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_FOOTER_INFO_EXCEPTION.getCode()));
  }

  @Test
  void getFooter__ShouldReturnOK() {
    //Arrange
    when(aemClient.getFooterInformation((any(FooterRequestAemDto.class))))
        .thenReturn(getFooterAemInformation());
    when(footerRequestMapper.toDto(any())).thenReturn(new FooterRequestAemDto());
    when(footerResponseMapper.toModel(any())).thenReturn(mockFooterInformation());

    //Act
    var footerResponse = footerOutPort
        .getFooterInformation(getFooterRequestGbEn());

    //AssertOF
    assertThat(footerResponse, notNullValue());
    assertEquals("&copy; 2022 Premier Inn", footerResponse.getCopyrightInfo());
    assertEquals("City breaks", footerResponse.getTabs().get(0).getName());
    assertEquals("", footerResponse.getTabs().get(0).getColumns().get(0).getName());
    assertEquals(
        "http://www.uat3.premierinn.digital/gb/en/hotels/england/greater-london/london/kensington.html",
        footerResponse.getTabs().get(0).getColumns().get(0).getLinkItems().get(0).getLinkSrc());
    assertEquals(false, footerResponse.getTabs().get(0).getColumns().get(0).getLinkItems().get(0)
        .getOpenInNewTab());
    assertEquals("Back to Homepage", footerResponse.getNewsletterSignup().getBookStayButtonText());
    assertEquals("/", footerResponse.getNewsletterSignup().getBookStayButtonUrl());
    assertEquals(
        "Great news – you’re now part of our Premier Inn mailing list, so keep an eye on your inbox!",
        footerResponse.getNewsletterSignup().getConfirmationText());
    assertEquals("Privacy", footerResponse.getBottomLinks().get(0).getName());
    verify(footerResponseMapper).toModel(any(FooterResponseAemDto.class));

  }

  private FooterResponse mockFooterInformation() {
    return FooterResponse.builder()
            .copyrightInfo("&copy; 2022 Premier Inn")
            .newsletterSignup(NewsletterSignup.builder()
                    .bookStayButtonText("Back to Homepage")
                    .bookStayButtonUrl("/")
                    .confirmationText("Great news – you’re now part of our Premier Inn mailing list, so keep an eye on your inbox!")
                    .build())
            .bottomLinks(of(LinkItems.builder()
                    .linkSrc("/gb/en/b.html")
                    .name("Privacy")
                    .openInNewTab(false)
                    .build()))
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

  private FooterResponseAemDto getFooterAemInformation() {
    return FooterResponseAemDto.builder()
            .copyright("&copy; 2022 Premier Inn")
            .newsletterSignup(NewsletterSignupAemDto.builder()
                    .bookStayButtonText("Back to Homepage")
                    .bookStayButtonUrl("/")
                    .confirmationText("Great news – you’re now part of our Premier Inn mailing list, so keep an eye on your inbox!")
                    .build())
            .bottomLinks(of(LinkItemsAemDto.builder()
                    .linkPath("/gb/en/b.html")
                    .linkText("Privacy")
                    .linkOpenNewTab("false")
                    .build()))
            .linkTabs(of(LinkTabsAemDto.builder()
                    .introDescription("<p>There are so many exciting things to do in the UK, so whether it’s last-minute weekend breaks or fun filled family holidays, we’ve got it all. No matter where your next adventure takes you, you can rest easy knowing you’ll get the same great-value rooms and friendly service at any of our 800+ hotels across the UK.</p>\\r\\n")
                    .introTitle("")
                    .tabTitle("City breaks")
                    .linkColumns(of(LinkColumnsAemDto.builder()
                            .columnTitle("")
                            .linkItems(of(LinkItemsAemDto.builder()
                                    .linkPath("http://www.uat3.premierinn.digital/gb/en/hotels/england/greater-london/london/kensington.html")
                                    .linkText("London - Kensington hotels")
                                    .linkOpenNewTab("false")
                                    .build()))
                            .build()))
                    .build()))
            .build();
  }

  private FooterRequest getFooterRequestGbEn() {
    return FooterRequest.builder()
            .country("gb")
            .language("en")
            .site("pi")
            .build();
  }
}
