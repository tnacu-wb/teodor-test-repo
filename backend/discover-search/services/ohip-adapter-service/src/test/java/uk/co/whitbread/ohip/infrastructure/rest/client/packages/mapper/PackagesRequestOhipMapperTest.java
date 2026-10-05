package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PackagesRequestOhipMapperImpl.class)
class PackagesRequestOhipMapperTest {

  public static final String HOTEL_ID = "TEST";
  public static final String START_DATE = "2022-12-30";
  public static final String END_DATE = "2022-12-31";
  public static final int ADULTS = 2;
  public static final int NR_NIGHTS = 2;
  public static final int CHILDREN = 0;

  @Autowired
  private PackagesRequestOhipMapper packagesRequestOhipMapper;


  private static PackagesRequest createPackagesRequest() {
    return PackagesRequest.builder()
        .hotelId(HOTEL_ID)
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adults(ADULTS)
        .nrNights(NR_NIGHTS)
        .children(CHILDREN)
        .build();
  }

  @Test
  void toPackageRequest__ShouldReturnOK() {
    //Act
    var packageRequest = packagesRequestOhipMapper.toOhipDto(createPackagesRequest());

    //Assert
    assertEquals(HOTEL_ID, packageRequest.getHotelId());
    assertEquals(START_DATE, packageRequest.getStartDate());
    assertEquals(END_DATE, packageRequest.getEndDate());
    assertEquals(ADULTS, packageRequest.getAdults());
    assertEquals(CHILDREN, packageRequest.getChildren());
  }
}
