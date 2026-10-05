package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MealResponseOhipMapperImpl.class)
class MealResponseOhipMapperTest {

  @Autowired
  MealResponseOhipMapper mealResponseOhipMapper;


  @Test
  void toPackagesResponse__ShouldReturnOK() throws IOException {
    //Act
    AtomicReference<String> currency = new AtomicReference<>();
    currency.set("GBP");
    var mealResponse = mealResponseOhipMapper.toModel(mockPackageCodeType(), currency);

    //Assert
    assertNotNull(mealResponse);
    assertNotNull(mealResponse.getId());

  }


  private static PackageCodeType mockPackageCodeType() throws IOException {
    var packageCodeType = new PackageCodeType();

    packageCodeType.setCode("BFADBF");

    var configPostingAttributesType = new ConfigPostingAttributesType();
    configPostingAttributesType.setCalculatedPrice(BigDecimal.valueOf(10L));

    var configPackagePrimaryDetailsType = new ConfigPackagePrimaryDetailsType();
    configPackagePrimaryDetailsType.setDescription("Premier Inn Breakfast");

    var configPackageTransactionType = new ConfigPackageTransactionType();
    configPackageTransactionType.setCurrency("GBP");

    var packageCodeHeaderType = new PackageCodeHeaderType();
    packageCodeHeaderType.setPostingAttributes(configPostingAttributesType);
    packageCodeHeaderType.setPrimaryDetails(configPackagePrimaryDetailsType);
    packageCodeHeaderType.setTransactionDetails(configPackageTransactionType);

    packageCodeType.setHeader(packageCodeHeaderType);
    return packageCodeType;
  }


}
