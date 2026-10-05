package uk.co.whitbread.ohip.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.CreateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.GuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileStayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;

public interface ProfileOutPort {

  List<String> createProfile(ProfileStayingGuestDetails createProfileRequest, String hotelId,
      String reservationId);

  void addProfile(AddProfileRequest addProfileRequest, String hotelId);

  CreateProfileRequest createProfileRequest(GuestDetails guestDetails, String hotelId);

  CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest);

  CompanyProfile getCompanyProfileByCorporateId(String corporateId);

  CompanyProfile getCompanyProfileByCompanyId(String companyId);

  void updateProfile(String hotelId, String reservationId,
      UpdateProfileRequest updateProfileRequestDto);
}
