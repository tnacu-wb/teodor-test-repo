package uk.co.whitbread.basket.infrastructure.rest.controller.email.model.in;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.email.in.EmailRequest;
import uk.co.whitbread.basket.domain.ports.primary.EmailNotificationInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.email.EmailNotificationController;
import uk.co.whitbread.basket.infrastructure.rest.controller.email.mapper.EmailMapper;

@ExtendWith(MockitoExtension.class)
class EmailNotificationControllerTest {

  @Mock
  EmailMapper emailMapper;
  @Mock
  EmailNotificationInPort emailNotificationInPort;
  @InjectMocks
  EmailNotificationController emailNotificationController;

  @Test
  void triggerEmailNotification__ShouldReturnAcceptedStatus() {
    //Arrange
   EmailRequestDto emailRequestDto = EmailRequestDto.builder()
       .bookingReference("bookingReference1")
       .emailRequestType("test@whitbread.com")
       .emailRequestType("AMEND").build();

   EmailRequest emailRequest = EmailRequest.builder()
       .bookingReference("bookingReference1")
       .emailRequestType("test@whitbread.com")
       .emailRequestType("AMEND").build();


    Mockito.when(emailMapper.toDomainModel(emailRequestDto))
        .thenReturn(emailRequest);

    //act
    var response = emailNotificationController.triggerEmailNotificationProcess(emailRequestDto);

    //Assert

    assertEquals(response.toString(), 202, response.getStatusCode().value());
  }
}