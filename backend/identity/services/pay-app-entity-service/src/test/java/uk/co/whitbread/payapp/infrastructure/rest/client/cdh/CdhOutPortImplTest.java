package uk.co.whitbread.payapp.infrastructure.rest.client.cdh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.payapp.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.mapper.PibaTetheredGuidResponseMapper;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CdhOutPortImplTest {

  @Mock
  private CdhClient cdhClient;

  @Mock
  private PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper;

  @InjectMocks
  private CdhOutPortImpl cdhOutPort;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getTetheredGuids_HappyCase_ReturnsMappedResponse() {
    // Arrange
    var cdhResponse = List.of(
        new PibaTetheredGuidResponse(111, 222, "GB", "guid1"),
        new PibaTetheredGuidResponse(111, 222, "GB", "guid2")
    );
    var expectedResponse = List.of(
        new TetheredGuidResponse(111, 222, "GB", "guid1"),
        new TetheredGuidResponse(111, 222, "GB", "guid2")
    );

    when(cdhClient.getTetheredGuids(anyString(), anyString(), anyString())).thenReturn(cdhResponse);
    when(pibaTetheredGuidResponseMapper.toDto(cdhResponse)).thenReturn(expectedResponse);

    // Act
    var result = cdhOutPort.getTetheredGuids("111", "222", "email");

    // Assert
    assertEquals(expectedResponse, result);
    verify(cdhClient).getTetheredGuids("111", "222", "email");
    verify(pibaTetheredGuidResponseMapper).toDto(cdhResponse);
  }

}
