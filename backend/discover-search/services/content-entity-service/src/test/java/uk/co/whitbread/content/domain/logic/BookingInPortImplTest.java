package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingDonation;
import uk.co.whitbread.content.domain.model.booking.out.BookingFlowStep;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.booking.out.InfoMessage;
import uk.co.whitbread.content.domain.model.booking.out.Item;
import uk.co.whitbread.content.domain.model.booking.out.Message;
import uk.co.whitbread.content.domain.model.booking.out.TermsAndConditions;
import uk.co.whitbread.content.domain.ports.secondary.BookingOutPort;

@ExtendWith(MockitoExtension.class)
class BookingInPortImplTest {

  @Mock
  private BookingOutPort bookingOutPort;

  @InjectMocks
  private BookingInPortImpl bookingInPort;

  @Test
  void getBookingInformation__ShouldReturnOk() {
    //Arrange
    when(this.bookingOutPort.getBookingInformation(any())).thenReturn(mockBookingInformation());

    //Act
    final var aemResponse = bookingInPort.getBookingInformation(createBookingInformationRequest());

    //Assert
    assertThat(aemResponse, notNullValue());
    assertEquals("booking/ukraine/dec-appeal-500x320.png",
        aemResponse.getBookingDonation().getImageSrc());
    assertEquals("Title", aemResponse.getBookingDonation().getName());
    assertEquals(
        "<p>We’ve been proudly raising money for Great Ormond Street Hospital Children’s Charity</p>"
        , aemResponse.getBookingDonation().getInformationBox());
    assertEquals("CHRTY3", aemResponse.getBookingDonation().getCharityCodes().get(0));
    assertEquals("SEMIFLEX", aemResponse.getTermsAndConditions().get(1).getRate());
    assertEquals("FLEXRATE", aemResponse.getTermsAndConditions().get(0).getRate());
    assertEquals("PI", aemResponse.getBrand());
    assertEquals("1", aemResponse.getBookingFlowSteps().get(0).getStep());
    assertEquals("BARTID", aemResponse.getUpsellItems().get(0).getBartId());
    assertEquals("CODE", aemResponse.getUpsellItems().get(0).getCode());
    assertEquals("3", aemResponse.getUpsellItems().get(0).getOrder());
    assertEquals("FLEXRATE", aemResponse.getInfoMessages().get(0).getRate());
    assertEquals("A", aemResponse.getInfoMessages().get(0).getRateCategory());
    assertEquals("PBF", aemResponse.getInfoMessages().get(0).getRateDisplaySet());
    assertEquals("Free cancellation up to 1pm on the day of arrival",
        aemResponse.getInfoMessages().get(0).getMessages().get(0).getMessage());
    verify(bookingOutPort, times(1)).getBookingInformation(any());
  }

  private BookingInformationRequest createBookingInformationRequest() {
    return BookingInformationRequest.builder()
        .country("gb")
        .language("en")
        .bookingFlowId("booking-a1")
        .hotelId("DUBSOU")
        .build();
  }

  private BookingDonation mockDonation(String imageSrc, String title, String informationBox) {
    return BookingDonation.builder().imageSrc(imageSrc).name(title).informationBox(informationBox)
        .description("test description").charityCodes(
            List.of("CHRTY3", "CHRTY4", "CHRTY5")).build();
  }

  private BookingInformation mockBookingInformation() {
    var donation = mockDonation("booking/ukraine/dec-appeal-500x320.png", "Title",
        "<p>We’ve been proudly raising money for Great Ormond Street Hospital Children’s Charity</p>");
    return BookingInformation.builder()
            .bookingDonation(donation)
            .brand("PI")
            .bookingFlowSteps(mockBookingFlowSteps())
            .termsAndConditions(mockTermsAndConditions())
            .upsellItems(mockItem())
            .infoMessages(mockInfoMessages())
            .build();
  }

  private List<InfoMessage> mockInfoMessages() {
    return List.of(InfoMessage.builder().rate("FLEXRATE").rateCategory("A").rateDisplaySet("PBF").messages(List.of(
        Message.builder().message("Free cancellation up to 1pm on the day of arrival").build())).build());
  }

  private List<BookingFlowStep> mockBookingFlowSteps() {
    return List.of(
        BookingFlowStep.builder().step("1").title("Ancillaries").build());
  }

  private List<TermsAndConditions> mockTermsAndConditions() {
    return List.of(TermsAndConditions.builder().rate("FLEXRATE").text(
                "<p>I have read, understand and accept the&nbsp;<a href=\\\"/content/pi/websites/desktop/gb/en/secured/terms/terms-uk.html\\\">Terms and Conditions</a>. Cancellations must be made before 1pm on your arrival day.</p>")
            .build(),
        TermsAndConditions.builder().rate("SEMIFLEX").text(
                "<p>I have read, understand and accept the&nbsp;<a href=\\\"/content/pi/websites/desktop/gb/en/secured/terms/terms-uk.html\\\">Terms and Conditions</a>.&nbsp;Cancellations must be made 28 days prior to the arrival date.</p>")
            .build());
  }

  private List<Item> mockItem() {
    return List.of(Item.builder()
            .code("CODE")
            .bartId("BARTID")
            .order("3")
            .build());
  }
}
