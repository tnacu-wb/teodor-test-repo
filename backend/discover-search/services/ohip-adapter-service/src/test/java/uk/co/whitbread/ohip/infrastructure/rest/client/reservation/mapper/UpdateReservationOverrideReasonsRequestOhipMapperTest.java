package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UpdateReservationOverrideReasonsRequestOhipMapperImpl.class)
class UpdateReservationOverrideReasonsRequestOhipMapperTest {

  @Autowired
  UpdateReservationOverrideReasonsRequestOhipMapper updateReservationOverrideReasonsRequestOhipMapper;

  @Test
  void updateReservationOverrideReasonsToChangeReservation__ShouldReturnOK() {
    //Arrange
    var updateReservationOverrideReasonsRequest = ReservationTestUtils.mockUpdateReservationOverrideReasonsRequest();

    //Act
    var updateReservationOverrideReasonsRequestDto = updateReservationOverrideReasonsRequestOhipMapper.toDto(
        updateReservationOverrideReasonsRequest);

    //Assert
    assertNotNull(updateReservationOverrideReasonsRequestDto);
    assertNotNull(
        updateReservationOverrideReasonsRequestDto.getReservations().get(0).getUserDefinedFields());
    assertEquals("UDFC08",
        updateReservationOverrideReasonsRequestDto.getReservations().get(0).getUserDefinedFields()
            .getCharacterUDFs().get(0)
            .getName());
    assertEquals("ILL,Medical Appointment,John Doe,Miriam More",
        updateReservationOverrideReasonsRequestDto.getReservations().get(0).getUserDefinedFields()
            .getCharacterUDFs().get(0)
            .getValue());
  }

}

