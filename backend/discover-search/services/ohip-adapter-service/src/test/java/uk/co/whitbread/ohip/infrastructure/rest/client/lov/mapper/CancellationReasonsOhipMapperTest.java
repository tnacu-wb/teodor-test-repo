package uk.co.whitbread.ohip.infrastructure.rest.client.lov.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.hasSize;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ItemType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ListOfValues;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ListOfValuesType;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.mapper.CancellationReasonsOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.mapper.CancellationReasonsOhipMapperImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CancellationReasonsOhipMapperImpl.class)
class CancellationReasonsOhipMapperTest {

  private static final String OTHER_REASON_CODE = "OTH";
  private static final String OTHER_REASON_NAME = "Other - See Comments Below";
  private static final String OTHER_REASON_DESCRIPTION = "Other - See Comments Below";

  private static final String ILLNESS_REASON_CODE = "ILL";
  private static final String ILLNESS_REASON_NAME = "MA Illness";
  private static final String ILLNESS_REASON_DESCRIPTION = "MA Illness";

  @Autowired
  CancellationReasonsOhipMapper cancellationReasonsOhipMapper;

  @Test
  void toCancellationReasonsResponse__ShouldReturnOK() {
    //Act
    var response = cancellationReasonsOhipMapper.toCancellationReasonsResponseModel(
        mockListOfValues());

    //Assert
    assertNotNull(response);
    assertThat(response.getCancellationReasons(), hasSize(2));

    // Reason with MA in description prefix should be the second element
    assertEquals(ILLNESS_REASON_CODE, response.getCancellationReasons().get(0).getCode());
    assertEquals(ILLNESS_REASON_NAME,
        response.getCancellationReasons().get(0).getName());
    assertEquals(ILLNESS_REASON_DESCRIPTION,
        response.getCancellationReasons().get(0).getDescription());
    assertTrue(response.getCancellationReasons().get(0).isActive());
    assertTrue(response.getCancellationReasons().get(0).isManagerApprovalNeeded());

    assertEquals(OTHER_REASON_CODE, response.getCancellationReasons().get(1).getCode());
    assertEquals(OTHER_REASON_NAME,
        response.getCancellationReasons().get(1).getName());
    assertEquals(OTHER_REASON_DESCRIPTION,
        response.getCancellationReasons().get(1).getDescription());
    assertTrue(response.getCancellationReasons().get(1).isActive());
    assertFalse(response.getCancellationReasons().get(1).isManagerApprovalNeeded());
  }

  private static ListOfValues mockListOfValues() {
    var listOfValues = new ListOfValues();
    var listOfValuesType = new ListOfValuesType();

    var itemTypeOther = new ItemType();
    itemTypeOther.setCode(OTHER_REASON_CODE);
    itemTypeOther.setName(OTHER_REASON_NAME);
    itemTypeOther.setDescription(OTHER_REASON_DESCRIPTION);
    itemTypeOther.setActive(Boolean.TRUE);

    var itemTypeIllness = new ItemType();
    itemTypeIllness.setCode(ILLNESS_REASON_CODE);
    itemTypeIllness.setName(ILLNESS_REASON_NAME);
    itemTypeIllness.setDescription(ILLNESS_REASON_DESCRIPTION);
    itemTypeIllness.setActive(Boolean.TRUE);
    listOfValuesType.setItems(List.of(itemTypeOther, itemTypeIllness));
    listOfValues.setListOfValues(listOfValuesType);

    return listOfValues;
  }

}
