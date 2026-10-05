package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelData;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LabelDataDto;

@ExtendWith(MockitoExtension.class)
class LabelDataResponseMapperTest {

  @InjectMocks
  LabelDataResponseMapperImpl mapper;

  @Test
  void toDto() {
    List<LabelData> labelDataList = new ArrayList<>();
    labelDataList.add(createLabelData("key1", "value1"));

    List<LabelDataDto> dtoList = mapper.toDto(labelDataList);
    assertEquals(labelDataList.size(), dtoList.size());
    assertEquals("key1", dtoList.get(0).getKey());
  }
  @Test
  void toDtoWithNullData() {
    List<LabelDataDto> dtoList = mapper.toDto(null);
    assertEquals(0, dtoList.size());
  }

  private LabelData createLabelData(String key, String value) {
    LabelData labelData = mock(LabelData.class);
    when(labelData.getKey()).thenReturn(key);
    when(labelData.getValue()).thenReturn(value);
    return labelData;
  }
}