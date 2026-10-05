package uk.co.whitbread.ohip.infrastructure.rest.controller.packages;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackage;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.Meal;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageCodes;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroups;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroupsResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.Packages;
import uk.co.whitbread.ohip.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.Restaurant;
import uk.co.whitbread.ohip.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.DonationPackagesRequestDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.DonationPackagesResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackageGroupRequestDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackageGroupResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackagesRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackagesResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.DonationPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.PackageCodesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.PackageGroupsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.PackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.DonationPackageDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.DonationPackagesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.MealDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackageCodesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackageGroupsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackagesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackagesGroupResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackagesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.RestaurantDto;

@ExtendWith(MockitoExtension.class)
class PackagesControllerTest {

  @InjectMocks
  PackagesController packagesControllerTest;

  @Mock
  private PackagesInPort packagesInPort;

  @Mock
  private PackagesRequestMapper packagesRequestMapper;

  @Mock
  private PackagesResponseDtoMapper packagesResponseDtoMapper;

  @Mock
  private DonationPackagesRequestDtoMapper donationPackagesRequestDtoMapper;

  @Mock
  private DonationPackagesResponseDtoMapper donationPackagesResponseDtoMapper;

  @Mock
  private PackageGroupResponseDtoMapper packageGroupResponseDtoMapper;

  @Mock
  private PackageGroupRequestDtoMapper packageGroupRequestDtoMapper;


  @Test
  void getHotelPackages_ShouldReturnOk() {
    //Arrange
    PackagesRequestDto packagesRequestDto = createPackagesRequestDto();

    Mockito.when(packagesRequestMapper.toModel(packagesRequestDto))
        .thenReturn(createPackagesRequest());
    Mockito.when(packagesInPort.getPackages(createPackagesRequest()))
        .thenReturn(getPackagesResponse());
    Mockito.when(packagesResponseDtoMapper.toDto(getPackagesResponse()))
        .thenReturn(getPackagesResponseDto());

    //act
    PackagesRequest request = packagesRequestMapper.toModel(packagesRequestDto);

    var packagesResponse = packagesInPort.getPackages(request);
    final PackagesResponseDto response = packagesControllerTest.getHotelPackages(packagesRequestDto);

    //Assert
    Assertions.assertNotNull(response);
  }

  @Test
  void getHotelDonationsPackagesDetails_ShouldReturnOk() {
    //Arrange
    DonationPackagesRequestDto donationPackagesRequestDto = createDonationPackagesRequestDto();

    Mockito.when(donationPackagesRequestDtoMapper.toModel(donationPackagesRequestDto))
        .thenReturn(createDonationPachagesRequest());
    Mockito.when(packagesInPort.getDonationPackagesDetails(createDonationPachagesRequest()))
        .thenReturn(getDonationPackagesResponse());
    Mockito.when(donationPackagesResponseDtoMapper.toDto(getDonationPackagesResponse()))
        .thenReturn(getDonationPackagesResponseDto());

    //act
    DonationPackagesRequest request = donationPackagesRequestDtoMapper.toModel(donationPackagesRequestDto);

    var packagesResponse = packagesInPort.getDonationPackagesDetails(request);
    final DonationPackagesResponseDto response = packagesControllerTest.getHotelDonationsPackagesDetails(donationPackagesRequestDto);

    //Assert
    Assertions.assertNotNull(response);
  }

  @Test
  void getHotelPackageGroups_WithPackageGroups_ShouldReturnOk() {
    PackageGroupsRequestDto packageGroupRequestDto = createRequestDtoWithPackageGroups();
    PackageGroupRequest packageGroupRequest = createPackageGroupRequest();
    PackageGroupsResponse packagesGroupDomain = createPackageGroupsResponse();
    PackagesGroupResponseDto packagesGroupResponseDto = createPackagesGroupResponseDto();

    Mockito.when(packageGroupRequestDtoMapper.toModel(packageGroupRequestDto))
        .thenReturn(packageGroupRequest);
    Mockito.when(packagesInPort.getPackagesGroups(packageGroupRequest))
        .thenReturn(packagesGroupDomain);
    Mockito.when(packageGroupResponseDtoMapper.toDto(packagesGroupDomain))
        .thenReturn(packagesGroupResponseDto);

    // Act
    var response = packagesControllerTest.getHotelPackageGroups(packageGroupRequestDto);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(packagesGroupResponseDto, response);
    Mockito.verify(packageGroupRequestDtoMapper).toModel(packageGroupRequestDto);
    Mockito.verify(packagesInPort).getPackagesGroups(packageGroupRequest);
    Mockito.verify(packageGroupResponseDtoMapper).toDto(packagesGroupDomain);
  }

  @Test
  void getHotelPackageGroups_WithPackageCodes_ShouldReturnOk() {
    PackageGroupsRequestDto packageGroupRequestDto = createRequestDtoWithPackageCodes();
    PackageGroupRequest packageGroupRequest = createPackageGroupRequest();
    PackageGroupsResponse packagesGroupDomain = createPackageGroupsResponse();
    PackagesGroupResponseDto packagesGroupResponseDto = createPackagesGroupResponseDto();

    Mockito.when(packageGroupRequestDtoMapper.toModel(packageGroupRequestDto))
        .thenReturn(packageGroupRequest);
    Mockito.when(packagesInPort.getPackagesGroups(packageGroupRequest))
        .thenReturn(packagesGroupDomain);
    Mockito.when(packageGroupResponseDtoMapper.toDto(packagesGroupDomain))
        .thenReturn(packagesGroupResponseDto);

    // Act
    var response = packagesControllerTest.getHotelPackageGroups(packageGroupRequestDto);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(packagesGroupResponseDto, response);
    Mockito.verify(packageGroupRequestDtoMapper).toModel(packageGroupRequestDto);
    Mockito.verify(packagesInPort).getPackagesGroups(packageGroupRequest);
    Mockito.verify(packageGroupResponseDtoMapper).toDto(packagesGroupDomain);
  }

  private PackageGroupRequest createPackageGroupRequest() {
    return PackageGroupRequest.builder()
        .hotelId("HEAPTI")
        .packageGroupList(Set.of("MDP", "DBR"))
        .build();
  }

  private PackageGroupsRequestDto createRequestDtoWithPackageGroups() {
    return PackageGroupsRequestDto.builder()
        .hotelId("HEAPTI")
        .packageGroupList(Set.of("MDP", "DBR"))
        .build();
  }

  private PackageGroupsRequestDto createRequestDtoWithPackageCodes() {
    var pkgCodeMDP = PackageCodesRequestDto.builder()
        .packageCodes(Set.of("MD2DIN", "MDBEVA", "MDBFST")).build();
    var pkgCodeDBR = PackageCodesRequestDto.builder().packageCodes(Set.of("DBBVPR", "DBDNPR"))
        .build();
    return PackageGroupsRequestDto.builder()
        .hotelId("HEAPTI")
        .packageCodeList(Set.of(pkgCodeMDP, pkgCodeDBR))
        .build();
  }

  private PackageGroupsResponse createPackageGroupsResponse() {
    PackageGroups group1 = PackageGroups.builder()
        .packageGroup("MDP")
        .packageGroupDescription("Meal Deal")
        .packageCodes(List.of(
            PackageCodes.builder()
                .packageCode("MD2DIN")
                .packageDescription("Meal Deal Dinner")
                .build(),
            PackageCodes.builder()
                .packageCode("MDBEVA")
                .packageDescription("Meal Deal Dinner Beverage")
                .build(),
            PackageCodes.builder()
                .packageCode("MDBFST")
                .packageDescription("Meal Deal Breakfast Food")
                .build()
        )).build();

    PackageGroups group2 = PackageGroups.builder()
        .packageGroup("DBR")
        .packageGroupDescription("Dinner and Drink Bundle")
        .packageCodes(List.of(
            PackageCodes.builder()
                .packageCode("DBBVPR")
                .packageDescription("Dinner Bundle Beverage")
                .build(),
            PackageCodes.builder()
                .packageCode("DBDNPR")
                .packageDescription("Dinner Bundle Food")
                .build()
        ))
        .build();
    return PackageGroupsResponse.builder()
        .packagesGroup(List.of(group1, group2))
        .build();
  }

  private PackagesGroupResponseDto createPackagesGroupResponseDto() {
    PackageGroupsDto group1 = PackageGroupsDto.builder()
        .packageGroup("MDP")
        .packageGroupDescription("Meal Deal")
        .packageCodes(List.of(
            PackageCodesDto.builder()
                .packageCode("MD2DIN")
                .packageDescription("Meal Deal Dinner")
                .build(),
            PackageCodesDto.builder()
                .packageCode("MDBEVA")
                .packageDescription("Meal Deal Dinner Beverage")
                .build(),
            PackageCodesDto.builder()
                .packageCode("MDBFST")
                .packageDescription("Meal Deal Breakfast Food")
                .build()
        ))
        .build();

    PackageGroupsDto group2 = PackageGroupsDto.builder()
        .packageGroup("DBR")
        .packageGroupDescription("Dinner and Drink Bundle")
        .packageCodes(List.of(
            PackageCodesDto.builder()
                .packageCode("DBBVPR")
                .packageDescription("Dinner Bundle Beverage")
                .build(),
            PackageCodesDto.builder()
                .packageCode("DBDNPR")
                .packageDescription("Dinner Bundle Food")
                .build()
        ))
        .build();

    return PackagesGroupResponseDto.builder()
        .packagesGroup(List.of(group1, group2))
        .build();
  }

  private DonationPackagesRequestDto createDonationPackagesRequestDto() {
    return DonationPackagesRequestDto.builder()
        .hotelId("FRAMTI")
        .packageCodes(List.of("CHRTY1, CHRTY2"))
        .build();
  }

  private DonationPackagesResponseDto getDonationPackagesResponseDto() {
    return DonationPackagesResponseDto.builder()
        .donationPackages(List.of(createDonationPackageDto()))
        .build();
  }

  private DonationPackageDto createDonationPackageDto() {
    return DonationPackageDto.builder()
        .unitPrice(new BigDecimal(1))
        .code("code")
        .currency("currency")
        .build();
  }

  private DonationPackagesResponse getDonationPackagesResponse() {
    return DonationPackagesResponse.builder()
        .donationPackages(List.of(createDonationPackage()))
        .build();
  }

  private DonationPackage createDonationPackage() {
    return DonationPackage.builder()
        .unitPrice(new BigDecimal(1))
        .code("code")
        .currency("currency")
        .build();
  }

  private DonationPackagesRequest createDonationPachagesRequest() {
    return DonationPackagesRequest.builder()
        .hotelId("FRAMTI")
        .packageCodes(List.of("CHRTY1, CHRTY2"))
        .build();
  }

  private PackagesResponseDto getPackagesResponseDto() {
    return PackagesResponseDto.builder()
        .restaurant(createRestaurantDto())
        .packages(createPackagesDto())
        .build();
  }

  private PackagesDto createPackagesDto() {
    return PackagesDto.builder()
        .meals(List.of(createMealDto()))
        .build();
  }

  private MealDto createMealDto() {
    return MealDto.builder()
        .price(new BigDecimal(1))
        .title("title")
        .id("id")
        .idImg("idImg")
        .idDesc("idDesc")
        .allergyInfoUrl("allergyInfoUrl")
        .currency("currency")
        .build();
  }

  private RestaurantDto createRestaurantDto() {
    return RestaurantDto.builder()
        .logoUrl("logoUrl")
        .restaurantNotFound(true)
        .noMealsFound(true)
        .build();
  }

  private PackagesResponse getPackagesResponse() {
    return PackagesResponse.builder()
        .restaurant(createRestaurant())
        .packages(createPackages())
        .build();
  }

  private Packages createPackages() {
    return Packages.builder()
        .meals(List.of(createMeal()))
        .build();
  }

  private Meal createMeal() {
    return Meal.builder()
        .price(new BigDecimal(1))
        .title("title")
        .id("id")
        .idImg("idImg")
        .idDesc("idDesc")
        .allergyInfoUrl("allergyInfoUrl")
        .currency("currency")
        .build();
  }

  private Restaurant createRestaurant() {
    return Restaurant.builder()
        .logoUrl("logoUrl")
        .restaurantNotFound(true)
        .noMealsFound(true)
        .build();
  }

  private PackagesRequest createPackagesRequest() {
    return PackagesRequest.builder()
        .hotelId("FRAMTI")
        .adults(1)
        .children(1)
        .startDate("startDate")
        .endDate("endDate")
        .nrNights(1)
        .build();
  }

  private PackagesRequestDto createPackagesRequestDto() {
    return PackagesRequestDto.builder()
        .hotelId("FRAMTI")
        .adults(1)
        .children(1)
        .startDate("startDate")
        .endDate("endDate")
        .nrNights(1)
        .build();
  }
}
