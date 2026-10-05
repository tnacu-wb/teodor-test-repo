package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.DetailDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.SummaryDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DailyPriceMapperImpl.class, PriceBreakdownMapperImpl.class})
class PriceBreakdownMapperTest {

  @Autowired
  PriceBreakdownMapper priceBreakdownMapper;

  @Test
  void toDomainModel__ShouldReturnOK() {
    //Arrange
    SummaryDto summary = SummaryDto.builder()
        .net(new BigDecimal(120.0))
        .gross(new BigDecimal(110.0))
        .currencyCode("GBP")
        .details(createDetails())
        .build();

    //Act
    var availabilityRoomPriceBreakdown = priceBreakdownMapper.toDomainModel(summary);

    //Assert
    assertEquals(new BigDecimal(120), availabilityRoomPriceBreakdown.getTotalNetAmount());
    assertEquals("GBP", availabilityRoomPriceBreakdown.getCurrencyCode());
    assertEquals("2022-01-01",
        availabilityRoomPriceBreakdown.getDailyPrices().get(0).getDate());
    assertEquals(new BigDecimal(55),
        availabilityRoomPriceBreakdown.getDailyPrices().get(0).getNetPrice());

  }

  private List<DetailDto> createDetails() {
    List<DetailDto> details = new LinkedList<>();
    details.add(DetailDto.builder()
        .summaryDate("2022-01-01")
        .tax(new BigDecimal(5.0))
        .gross(new BigDecimal(60.0))
        .net(new BigDecimal(55.0))
        .build());
    details.add(DetailDto.builder()
        .summaryDate("2022-01-02")
        .tax(new BigDecimal(5.0))
        .gross(new BigDecimal(60.0))
        .net(new BigDecimal(55.0))
        .build());

    return details;
  }

}