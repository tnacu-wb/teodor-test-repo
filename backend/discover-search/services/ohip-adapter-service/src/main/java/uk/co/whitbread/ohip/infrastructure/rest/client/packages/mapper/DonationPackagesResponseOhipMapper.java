package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.HotelPackageSchedulePriceType;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackage;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;

@Component
@RequiredArgsConstructor
public class DonationPackagesResponseOhipMapper {

  public DonationPackagesResponse toModel(
      final PackagesResponseOhipDto donationPackagesResponseOhipDto) {
    List<DonationPackage> donationPackages =
        donationPackagesResponseOhipDto.getPackageCodesList().getPackageCodes().stream()
            .flatMap(packageCode -> packageCode.getPackageCodeInfo()
                .stream()
                .map(packageCodeType -> {
                  var currency = packageCodeType.getHeader().getTransactionDetails().getCurrency();
                  var unitPrice = packageCodeType.getSchedules().stream()
                      .flatMap(schedule -> schedule.getSchedulePrices().stream()
                          .map(HotelPackageSchedulePriceType::getUnitPrice))
                      .findFirst()
                      .orElse(null);
                  return DonationPackage.builder()
                      .code(packageCodeType.getCode())
                      .unitPrice(unitPrice)
                      .currency(currency)
                      .build();
                })
            ).toList();
    return DonationPackagesResponse.builder().donationPackages(donationPackages).build();
  }
}
