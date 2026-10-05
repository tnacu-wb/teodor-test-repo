package uk.co.whitbread.ohip.domain.logic;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileStayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;
import uk.co.whitbread.ohip.domain.ports.primary.ProfileInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.ProfileOutPort;

@RequiredArgsConstructor
@Slf4j
public class ProfileInPortImpl implements ProfileInPort {

  private final ProfileOutPort profileOutPort;

  @Override
  public List<String> createProfile(ProfileStayingGuestDetails stayingGuestDetails,
      String hotelId, String reservationId) {
    return profileOutPort.createProfile(stayingGuestDetails, hotelId, reservationId);
  }

  @Override
  public void addProfile(AddProfileRequest addProfileRequest, String hotelId) {
    profileOutPort.addProfile(addProfileRequest, hotelId);
  }

  @Override
  public CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest) {
    return profileOutPort.getCompaniesProfile(companiesProfileRequest);
  }

  @Override
  public CompanyProfile getCompanyProfileByCorporateId(final String corporateId) {
    return profileOutPort.getCompanyProfileByCorporateId(corporateId);
  }

  @Override
  public CompanyProfile getCompanyProfileByCompanyId(String companyId) {
    return profileOutPort.getCompanyProfileByCompanyId(companyId);
  }

  @Override
  public void updateProfile(String hotelId, String reservationId,
      UpdateProfileRequest updateProfileRequest) {
    profileOutPort.updateProfile(hotelId, reservationId, updateProfileRequest);
  }
}
