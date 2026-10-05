package uk.co.whitbread.content.infrastructure.rest.client.footer;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COOKIE_POLICIES_EXCEPTION;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.footer.adapter.FooterAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.FooterResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkColumnsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkItemsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkTabsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.NewsletterSignupAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.out.FooterRequestAemDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class FooterAemClientTests {

  @InjectMocks
  private FooterAemClient aemClient;

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Test
  void getFooterInfo__ShouldReturnOK() {
    initWebClient();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(FooterResponseAemDto.class)).thenReturn(
        mockFooterAemInformation());

    //Act
    final var footerAEMResponse =
        aemClient.getFooterInformation(mockFooterRequestGbEn());

    //Assert
    assertThat(footerAEMResponse, notNullValue());
  }

  @Test
  void getFooterInfo_ShouldReturnException() {
    initWebClient();
    var exception = new AemResponseException(
        AEM_COOKIE_POLICIES_EXCEPTION,
        "message",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> aemClient.getFooterInformation(mockFooterRequestGbEn()));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_COOKIE_POLICIES_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("message"));
    assertThat(actual.getErrorCode(), is(AEM_COOKIE_POLICIES_EXCEPTION.getCode()));
  }

  private void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }


  private Mono<FooterResponseAemDto> mockFooterAemInformation() {
    var response = FooterResponseAemDto.builder()
            .copyright("&copy; 2022 Premier Inn")
            .bottomLinks(of(LinkItemsAemDto.builder()
                .linkPath("/gb/en/b.html")
                .linkText("Privacy")
                .linkOpenNewTab("false")
                .build()))
            .newsletterSignup(NewsletterSignupAemDto.builder()
                    .bookStayButtonText("Back to Homepage")
                    .bookStayButtonUrl("/")
                    .confirmationText("Great news – you’re now part of our Premier Inn mailing list, so keep an eye on your inbox!")
                    .build())
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
    return Mono.just(response);
  }

  private FooterRequestAemDto mockFooterRequestGbEn() {
    return FooterRequestAemDto.builder()
            .country("gb")
            .language("en")
            .site("pi")
            .build();
  }


}
