package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplementResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.OccupancySupplementInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.OccupancySupplementDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.OccupancySupplementDtoMapperImpl;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MultiOccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.OccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MultiOccupancySupplementResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.OccupancySupplementResponseDto;

@ExtendWith(MockitoExtension.class)
class OccupancySupplementControllerTest {
  @Mock
  private OccupancySupplementInPort occupancySupplementInPort;
  @Spy
  private OccupancySupplementDtoMapper occupancySupplementDtoMapper = new OccupancySupplementDtoMapperImpl();
  @InjectMocks
  private OccupancySupplementController occupancySupplementController;

  @Test
  void shouldRetrieveOccupancySupplements() {
    //Arrange
    var request = OccupancySupplementRequestDto.builder()
        .hotelId("FRAMTI")
        .build();
    var mockedResponse = OccupancySupplementResponse.builder()
        .hotelId("FRAMTI")
        .pricing(BigDecimal.ZERO)
        .build();
    var expectedResponse = OccupancySupplementResponseDto.builder()
        .hotelId("FRAMTI")
        .pricing(BigDecimal.ZERO)
        .build();
    when(occupancySupplementInPort.getOccupancySupplementPricing(any()))
        .thenReturn(mockedResponse);

    //Act
    var response = occupancySupplementController.getOccupancySupplementPricing(request);

    //Assert
    assertThat(response).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(expectedResponse);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldRetrieveMultiOccupancySupplements(boolean isDictionaryFormat) {
    //Arrange
    var request = MultiOccupancySupplementRequestDto.builder()
        .hotelIds(List.of(
            OccupancySupplementRequestDto.builder()
                .hotelId("FRAMTI")
                .build(),
            OccupancySupplementRequestDto.builder()
                .hotelId("GATGAT")
                .build()
        )).build();
    var expectedDictionaryResponse = MultiOccupancySupplementResponseDto.builder()
        .dictionary(Map.of("FRAMTI", BigDecimal.ZERO, "GATGAT", BigDecimal.TEN))
        .build();
    var expectedListResponse =
        MultiOccupancySupplementResponseDto.builder()
            .list(List.of(OccupancySupplementResponseDto.builder().hotelId("FRAMTI").pricing(BigDecimal.ZERO).build(),
                OccupancySupplementResponseDto.builder().hotelId("GATGAT").pricing(BigDecimal.TEN).build()))
            .build();
    when(occupancySupplementInPort.getOccupancySupplementPricing(any()))
        .thenReturn(OccupancySupplementResponse.builder()
            .hotelId("FRAMTI")
            .pricing(BigDecimal.ZERO)
            .build())
        .thenReturn(OccupancySupplementResponse.builder()
            .hotelId("GATGAT")
            .pricing(BigDecimal.TEN)
            .build());

    //Act
    var response = occupancySupplementController.getMultiOccupancySupplementPricing(request, isDictionaryFormat);

    //Assert
    if (isDictionaryFormat) {
      assertThat(response).usingRecursiveComparison()
          .isEqualTo(expectedDictionaryResponse);
    } else {
      assertThat(response).usingRecursiveComparison()
          .isEqualTo(expectedListResponse);
    }
  }

}