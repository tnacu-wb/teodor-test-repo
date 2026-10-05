package uk.co.whitbread.availabilitycacheservice.infrastructure.feign;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.snowdrop.HotelDetailsDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.snowdrop.ItemLatLonDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"increase-batch-size"})
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
@Slf4j
@Disabled

public class SnowdropFeignClientTest {

  @Autowired
  SnowdropFeignClient client;

  @Test
  public void getHotelsFromSnowdrop() {
    HotelDetailsDto hotelDetailsDto = HotelDetailsDto.builder().code("LONLEI")
        .name("London Leicester Square").brand("PI").distance(727.0)
        .location(new ItemLatLonDto(51.511143, -0.13035)).build();

    List<HotelDetailsDto> response = client.getHotelsFromSnowdrop("ChIJdd4hrwug2EcRmSrV3Vo6llI", "50mi");

    assertThat(response).isNotEmpty().contains(hotelDetailsDto);
    log.info("HotelDetails={}", response.get(0));
  }

}
