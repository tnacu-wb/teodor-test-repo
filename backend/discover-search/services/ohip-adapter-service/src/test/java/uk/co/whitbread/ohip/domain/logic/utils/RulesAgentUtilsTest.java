package uk.co.whitbread.ohip.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.logic.utils.GuestInfoUtils;
import uk.co.whitbread.ohip.domain.logic.utils.RulesAgentUtils;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GuestInfoUtils.class)
class RulesAgentUtilsTest {

  @Test
  void convertWbRoomTypesToPmsRoomTypes__Success(){

    //Arrange
    var inputRservationGuest = mockRoomSubstitutionResponse();

    //Act
    List<String> roomType = RulesAgentUtils.convertWbRoomTypesToPmsRoomTypes(mockRoomSubstitutionResponse());

    //Assert
    assertNotNull(roomType);
    assertEquals(7, roomType.size());
    assertEquals("LOWTWN", roomType.get(5));

  }

  private List<RoomSubstitutionRuleResponse> mockRoomSubstitutionResponse() {
    return List.of(RoomSubstitutionRuleResponse.builder().requestDetails(RoomSubstitutionRequestDetails.builder().roomType("DB").pms("OP").build())
        .substitutionList(mockRoomSubstitutionList(List.of("DOUBLE", "NWDSPL", "PDBZPL", "FMTRPL"))).build(),
        RoomSubstitutionRuleResponse.builder().requestDetails(RoomSubstitutionRequestDetails.builder().roomType("DIS").pms("OP").build())
            .substitutionList(mockRoomSubstitutionList(List.of("ACCSGL", "LOWTWN", "ACCWIN"))).build()
        );
  }

  private List<RoomSubstitution> mockRoomSubstitutionList (List<String> roomTypes){
    return roomTypes.stream().map(type -> RoomSubstitution.builder().type(type).build()).toList();
  }
}
