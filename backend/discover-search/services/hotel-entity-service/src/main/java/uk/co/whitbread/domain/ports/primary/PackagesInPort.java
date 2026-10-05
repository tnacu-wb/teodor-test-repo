package uk.co.whitbread.domain.ports.primary;

import uk.co.whitbread.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;

public interface PackagesInPort {

  PackagesResponse getPackages(PackagesRequest packagesRequest);

  DonationPackagesResponse getDonationPackageDetails(
      DonationPackagesRequest donationPackagesRequest);

  MealsInfoResponse getUpsellItemsAndSoftBundles(String hotelId, String country, String language);
}
