package uk.co.whitbread.content.domain.logic;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.co.whitbread.content.domain.ports.secondary.FooterOutPort;

@ExtendWith(MockitoExtension.class)
class FooterInPortImplTest {

  @Mock
  private FooterOutPort footerOutPort;

  @InjectMocks
  private FooterInPortImpl footerInPort;

  @Test
  void getFooterInformation__ShouldReturnOk() {
    //Arrange
    when(this.footerOutPort.getFooterInformation(any())).thenReturn(mockFooterInformation());

    //Act
    final var aemResponse = footerInPort.getFooterInformation(createFooterRequest());

    //Assert
    assertEquals("&copy; 2022 Premier Inn", aemResponse.getCopyrightInfo());
    assertEquals("City breaks", aemResponse.getTabs().get(0).getName());
    assertEquals("", aemResponse.getTabs().get(0).getColumns().get(0).getName());
    assertEquals(false,
        aemResponse.getTabs().get(0).getColumns().get(0).getLinkItems().get(0).getOpenInNewTab());
    assertEquals("Back to Homepage", aemResponse.getNewsletterSignup().getBookStayButtonText());
    assertEquals("/", aemResponse.getNewsletterSignup().getBookStayButtonUrl());
    assertEquals(
        "Great news – you’re now part of our Premier Inn mailing list, so keep an eye on your inbox!",
        aemResponse.getNewsletterSignup().getConfirmationText());

    verify(footerOutPort, times(1)).getFooterInformation(any());
  }

  private FooterRequest createFooterRequest() {
    return FooterRequest.builder()
            .country("gb")
            .language("en")
            .site("pi")
            .build();
  }

  private FooterResponse mockFooterInformation() {
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


}
