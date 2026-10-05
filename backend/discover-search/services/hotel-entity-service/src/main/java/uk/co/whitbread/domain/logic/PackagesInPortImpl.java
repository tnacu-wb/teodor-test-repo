package uk.co.whitbread.domain.logic;

import lombok.RequiredArgsConstructor;
import uk.co.whitbread.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.domain.ports.secondary.PackagesOutPort;

@RequiredArgsConstructor
public class PackagesInPortImpl implements PackagesInPort {

  private final PackagesOutPort packagesOutPort;

  @Override
  public PackagesResponse getPackages(PackagesRequest packagesRequest) {
    return packagesOutPort.getPackages(packagesRequest);
  }

  @Override
  public DonationPackagesResponse getDonationPackageDetails(
      DonationPackagesRequest donationPackagesRequest) {
    return packagesOutPort.getDonationPackageDetails(donationPackagesRequest);
  }

  @Override
  public MealsInfoResponse getUpsellItemsAndSoftBundles(String hotelId, String country,
      String language) {
    return packagesOutPort.getUpsellItemsAndSoftBundles(hotelId, country, language);
  }
}
