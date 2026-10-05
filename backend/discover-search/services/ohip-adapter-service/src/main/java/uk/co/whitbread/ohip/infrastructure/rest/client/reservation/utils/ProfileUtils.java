package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;

public final class ProfileUtils {


  private static final Predicate<Profile> checkEmail = orig -> {
    var emails = orig.getProfileDetails().getEmails();
    if (Objects.isNull(emails)) {
      return true;
    } else {
      if (Objects.isNull(emails.getEmailInfo())) {
        return true;
      } else {
        return Objects.isNull(emails.getEmailInfo().get(0).getEmail().getEmailAddress());
      }
    }
  };

  private static final Predicate<Profile> checkPostalCode = orig -> {
    var adressInfo = orig.getProfileDetails().getAddresses().getAddressInfo();
    if (Objects.isNull(adressInfo)) {
      return true;
    } else {
      if (Objects.isNull(adressInfo.get(0).getAddress())) {
        return true;
      } else {
        return Objects.isNull(adressInfo.get(0).getAddress().getPostalCode());
      }
    }
  };

  private static final BiPredicate<Profile, Profile> compareTitle
      = (orig, temp) -> orig.getProfileDetails().getCustomer().getPersonName().get(0)
      .getNameTitle()
      .equals(temp.getProfileDetails().getCustomer().getPersonName().get(0).getNameTitle());

  private static final BiPredicate<Profile, Profile> compareGivenName
      = (orig, temp) -> orig.getProfileDetails().getCustomer().getPersonName().get(0)
      .getGivenName()
      .equals(temp.getProfileDetails().getCustomer().getPersonName().get(0).getGivenName());

  private static final BiPredicate<Profile, Profile> compareSurname
      = (orig, temp) -> orig.getProfileDetails().getCustomer().getPersonName().get(0).getSurname()
      .equals(temp.getProfileDetails().getCustomer().getPersonName().get(0).getSurname());

  private static final BiPredicate<Profile, Profile> compareEmail = (orig, temp) -> {
    if (!checkEmail.test(orig) && !checkEmail.test(temp)) {
      return orig.getProfileDetails().getEmails().getEmailInfo().get(0).getEmail()
          .getEmailAddress().equals(
              temp.getProfileDetails().getEmails().getEmailInfo().get(0).getEmail()
                  .getEmailAddress());
    } else {
      return false;
    }
  };

  private static final BiPredicate<Profile, Profile> comparePostalCode = (orig, temp) -> {
    if (!checkPostalCode.test(orig) && !checkPostalCode.test(temp)) {
      return orig.getProfileDetails().getAddresses().getAddressInfo().get(0)
          .getAddress().getPostalCode().equals(
              temp.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress()
                  .getPostalCode());
    } else {
      return false;
    }
  };

  private ProfileUtils() {
  }

  public static boolean needToUpdateOriginalProfile(Profile originalProfile, Profile temporaryProfile) {
    var checkRequiredFieldsIsEqual = compareTitle.and(compareGivenName)
        .and(compareSurname);

    if (checkRequiredFieldsIsEqual.test(originalProfile, temporaryProfile)) {
      if (checkEmail.test(originalProfile) && !checkEmail.test(temporaryProfile)
          && checkPostalCode.test(originalProfile) && !checkPostalCode.test(temporaryProfile)) {
        return true;
      }
      if (checkEmail.test(originalProfile) && !checkEmail.test(temporaryProfile)
          && checkIfPostalCodesAreEquals(originalProfile, temporaryProfile)) {
        return true;
      }
      if (checkPostalCode.test(originalProfile) && !checkPostalCode.test(temporaryProfile)
          && checkIfEmailsAreEquals(originalProfile, temporaryProfile)) {
        return true;
      }
      return checkIfEmailsAreEquals(originalProfile, temporaryProfile)
          && checkIfPostalCodesAreEquals(originalProfile, temporaryProfile);
    }
    return false;

  }

  static boolean checkIfEmailsAreEquals(Profile originalProfile, Profile temporaryProfile) {
    return (checkEmail.test(originalProfile) && checkEmail.test(temporaryProfile))
        || compareEmail.test(originalProfile, temporaryProfile);
  }

  static boolean checkIfPostalCodesAreEquals(Profile originalProfile, Profile temporaryProfile) {
    return (checkPostalCode.test(originalProfile) && checkPostalCode.test(temporaryProfile))
        || comparePostalCode.test(originalProfile, temporaryProfile);
  }

}
