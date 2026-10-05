package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackagesInfoPackageCodesList;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;

@ExtendWith(MockitoExtension.class)
class DonationPackagesResponseOhipMapperTest {

  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
  public static final String MOCK_CHARITY_PACKAGES_RESPONSE_JSON = "__files/ohip_charityPackageCodesList_object.json";

  @Test
  void toDonationPackagesResponse__ShouldReturnOK() throws IOException {
    //Arrange
    var packagesResponseOhipMapper = new DonationPackagesResponseOhipMapper();

    //Act
    var packageResponse = packagesResponseOhipMapper.toModel(getDonationPackagesFromOhip());

    //Assert
    assertNotNull(packageResponse);
    assertNotNull(packageResponse.getDonationPackages());

    var firstCharityPackage = packageResponse.getDonationPackages().get(0);
    assertNotNull(firstCharityPackage);
    assertEquals("CHRTY1", firstCharityPackage.getCode());
    assertEquals("EUR", firstCharityPackage.getCurrency());
    assertEquals(BigDecimal.valueOf(3), firstCharityPackage.getUnitPrice());

  }

  private PackagesResponseOhipDto getDonationPackagesFromOhip() throws IOException {
    return PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(PackagesResponseOhipMapperTest.class.getClassLoader()
            .getResource(MOCK_CHARITY_PACKAGES_RESPONSE_JSON), PackagesInfoPackageCodesList.class))
        .build();
  }
}
