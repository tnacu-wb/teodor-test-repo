package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroupsResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.PackagesResponse;

public interface PackagesInPort {

  PackagesResponse getPackages(PackagesRequest packagesRequest);

  DonationPackagesResponse getDonationPackagesDetails(DonationPackagesRequest donationPackagesRequest);

  PackageGroupsResponse getPackagesGroups(PackageGroupRequest packageGroupRequest);
}
