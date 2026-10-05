package uk.co.whitbread.rules.agent.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.in.OccupancySupplementRequest;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplement;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.ports.secondary.OccupancySupplementRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class OccupancySupplementInPortImplTest {

  @Mock
  private OccupancySupplementRepositoryOutPort occupancySupplementRepositoryOutPort;

  @InjectMocks
  private OccupancySupplementInPortImpl occupancySupplementInPort;


  @Test
  void getOccupancySupplementPricing__shouldReturnOk() {
    //Arrange
    var request = OccupancySupplementRequest.builder()
        .hotelId("FRAMTI")
        .build();
    var repositoryResponse = OccupancySupplement.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .lastModifiedAt(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .pricing(BigDecimal.valueOf(2))
        .hotelId("FRAMTI")
        .build();
    when(occupancySupplementRepositoryOutPort.findRule(request.getHotelId()))
        .thenReturn(Optional.of(repositoryResponse));

    //Act
    var response = occupancySupplementInPort.getOccupancySupplementPricing(request);

    //Assert
    assertThat(response, notNullValue());
    AssertionsForClassTypes.assertThat(response.getPricing()).isEqualTo(repositoryResponse.getPricing());
    AssertionsForClassTypes.assertThat(response.getHotelId()).isEqualTo(repositoryResponse.getHotelId());
    verifyNoMoreInteractions(occupancySupplementRepositoryOutPort);
  }

  @Test
  void getOccupancySupplementPricing__shouldReturnDefault() {
    //Arrange
    var request = OccupancySupplementRequest.builder()
        .hotelId("FRAMTI")
        .build();
    when(occupancySupplementRepositoryOutPort.findRule(any()))
        .thenReturn(Optional.empty());

    //Act
    var response = occupancySupplementInPort.getOccupancySupplementPricing(request);

    //Assert
    assertThat(response, notNullValue());
    AssertionsForClassTypes.assertThat(response.getPricing()).isEqualTo(BigDecimal.ZERO);
    AssertionsForClassTypes.assertThat(response.getHotelId()).isEqualTo(request.getHotelId());
    verifyNoMoreInteractions(occupancySupplementRepositoryOutPort);
  }
}
