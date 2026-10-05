package uk.co.whitbread.ohip.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroupsResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.ohip.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.PackagesOutPort;

@Slf4j
@RequiredArgsConstructor
public class PackagesInPortImpl implements PackagesInPort {

  private final PackagesOutPort packagesOutPort;

  @Override
  public PackagesResponse getPackages(PackagesRequest packagesRequest) {
    log.debug("Entered getPackages for hotelId={}", packagesRequest.getHotelId());
    return packagesOutPort.getPackages(packagesRequest);
  }

  @Override
  public DonationPackagesResponse getDonationPackagesDetails(DonationPackagesRequest donationPackagesRequest) {
    log.debug("Entered getDonationPackagesDetails for hotelId={}", donationPackagesRequest.getHotelId());
    return packagesOutPort.getDonationPackagesDetails(donationPackagesRequest);
  }

  @Override
  public PackageGroupsResponse getPackagesGroups(PackageGroupRequest packageGroupRequest) {
    log.debug("Entered getPackagesGroups for hotelId={}", packageGroupRequest.getHotelId());
    return packagesOutPort.getPackagesGroups(packageGroupRequest);
  }

}
