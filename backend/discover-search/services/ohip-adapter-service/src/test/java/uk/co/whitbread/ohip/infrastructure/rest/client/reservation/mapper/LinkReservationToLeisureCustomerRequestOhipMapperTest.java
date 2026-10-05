package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PI_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_09;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_35;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LinkReservationToLeisureCustomerRequestOhipMapperImpl.class)
class LinkReservationToLeisureCustomerRequestOhipMapperTest {

  @Autowired
  LinkReservationToLeisureCustomerRequestOhipMapper linkReservationToLeisureCustomerRequestOhipMapper;

  @Test
  void linkReservationToLeisureCustomerRequestToChangeReservation__ShouldReturnOK() {
    //Arrange
    var linkReservationToLeisureCustomerRequest  = ReservationTestUtils.mockLinkReservationToLeisureCustomerRequest();

    //Act
    var linkReservationToLeisureCustomerRequestDto = linkReservationToLeisureCustomerRequestOhipMapper.toDto(
        linkReservationToLeisureCustomerRequest);

    //Assert
    assertNotNull(linkReservationToLeisureCustomerRequestDto);
    assertNotNull(
        linkReservationToLeisureCustomerRequestDto.getReservations().get(0).getUserDefinedFields());
    assertEquals(2, linkReservationToLeisureCustomerRequestDto.getReservations().get(0).getUserDefinedFields()
        .getCharacterUDFs().size());
    assertEquals(UDFC_35,
        linkReservationToLeisureCustomerRequestDto.getReservations().get(0).getUserDefinedFields()
            .getCharacterUDFs().get(0)
            .getName());
    assertEquals(linkReservationToLeisureCustomerRequest.getCustomerAccountId(),
        linkReservationToLeisureCustomerRequestDto.getReservations().get(0).getUserDefinedFields()
            .getCharacterUDFs().get(0)
            .getValue());
    assertEquals(UDFC_09,
        linkReservationToLeisureCustomerRequestDto.getReservations().get(0).getUserDefinedFields()
            .getCharacterUDFs().get(1)
            .getName());
    assertEquals(PI_CHANNEL,
        linkReservationToLeisureCustomerRequestDto.getReservations().get(0).getUserDefinedFields()
            .getCharacterUDFs().get(1)
            .getValue());
  }
}