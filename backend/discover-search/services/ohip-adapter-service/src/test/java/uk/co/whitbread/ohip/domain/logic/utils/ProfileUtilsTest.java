package uk.co.whitbread.ohip.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.ProfileUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ProfileUtils.class)
class ProfileUtilsTest {

  @Test
  void  needToUpdateOriginalProfile_returnTrue_updateEmail_fromBlank(){
    var originalProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", null, null);
    var tempProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "tester@testerson.ro", null);

    var result = ProfileUtils.needToUpdateOriginalProfile(originalProfile, tempProfile);

    assertTrue(result);

  }

  @Test
  void  needToUpdateOriginalProfile_returnTrue_postalCode_fromBlank(){
    var originalProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", null, null);
    var tempProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", null, "987");

    var result = ProfileUtils.needToUpdateOriginalProfile(originalProfile, tempProfile);

    assertTrue(result);

  }

  @Test
  void  needToUpdateOriginalProfile_returnTrue_updateEmail_postalCode_fromBlank(){
    var originalProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", null, null);
    var tempProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "tester@testerson.ro", "1234");

    var result = ProfileUtils.needToUpdateOriginalProfile(originalProfile, tempProfile);

    assertTrue(result);

  }

  @Test
  void  needToUpdateOriginalProfile_returnFalse_updateEmail_postalCode(){
    var originalProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "testerrrrrr@testerson.ro", "7865");
    var tempProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "tester@testerson.ro", "1234");

    var result = ProfileUtils.needToUpdateOriginalProfile(originalProfile, tempProfile);

    assertFalse(result);

  }

  @Test
  void  needToUpdateOriginalProfile_returnFalse_updateEmail_fromValueToBlank(){
    var originalProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "testerrrrrr@testerson.ro", "7865");
    var tempProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", null, "7865");

    var result = ProfileUtils.needToUpdateOriginalProfile(originalProfile, tempProfile);

    assertFalse(result);

  }

  @Test
  void  needToUpdateOriginalProfile_returnFalse_updateEmail_postalCode_fromValueToBlank(){
    var originalProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "testerrrrrr@testerson.ro", "7865");
    var tempProfile =  mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "testerrrrrr@testerson.ro", null);

    var result = ProfileUtils.needToUpdateOriginalProfile(originalProfile, tempProfile);

    assertFalse(result);

  }

  private Profile mockProfileToCompare(String profileId, String title, String givenName, String surname, String email, String postalCode) {
    Profile result = new Profile();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType uniqueIDType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId(profileId);
    uniqueIDType.setType("Profile");
    result.setProfileIdList(List.of(uniqueIDType));
    var crmProfileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    var crmCustomer = new CustomerType();
    var crmPersonName = new PersonNameType();
    crmPersonName.setNameTitle(title);
    crmPersonName.setGivenName(givenName);
    crmPersonName.setSurname(surname);
    crmCustomer.setPersonName(List.of(crmPersonName));
    crmProfileType.setCustomer(crmCustomer);
    var emailDetails = new CompanyProfileTypeEmails();
    var emailInfo = new EmailInfoType();
    var emailType = new EmailType();
    emailType.setEmailAddress(email);
    emailInfo.setEmail(emailType);
    emailDetails.emailInfo(List.of(emailInfo));
    crmProfileType.setEmails(emailDetails);
    var address = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType();
    address.setPostalCode(postalCode);
    var addressInfo = new AddressInfoType();
    addressInfo.setAddress(address);
    var profileAddress = new ProfileTypeAddresses();
    profileAddress.setAddressInfo(List.of(addressInfo));
    crmProfileType.setAddresses(profileAddress);
    result.setProfileDetails(crmProfileType);

    return result;
  }

}
