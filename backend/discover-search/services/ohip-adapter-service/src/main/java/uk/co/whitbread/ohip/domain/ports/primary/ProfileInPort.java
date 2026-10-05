package uk.co.whitbread.ohip.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileStayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;

public interface ProfileInPort {

  List<String> createProfile(ProfileStayingGuestDetails createProfileRequest, String hotelId,
      String reservationId);

  void addProfile(AddProfileRequest addProfileRequest, String hotelId);

  CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest);

  CompanyProfile getCompanyProfileByCorporateId(String corporateId);

  CompanyProfile getCompanyProfileByCompanyId(String corporateId);


  void updateProfile(String hotelId, String reservationId,
      UpdateProfileRequest updateProfileRequestDto);
}
