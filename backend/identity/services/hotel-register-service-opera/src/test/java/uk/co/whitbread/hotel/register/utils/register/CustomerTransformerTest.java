package uk.co.whitbread.hotel.register.utils.register;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.hotel.register.model.Address;
import uk.co.whitbread.hotel.register.model.ContactDetail;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.shared.azureemail.model.PiRegister;

public class CustomerTransformerTest {

  private CustomerTransformer customerTransformer;

  @BeforeEach
  public void setUp() throws Exception {
    customerTransformer = Mappers.getMapper(CustomerTransformer.class);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"Country name"})
  void toPiRegister(String country) {
    final Customer customer = random(Customer.class);
    final ContactDetail contactDetail = customer.getContactDetail();
    final Address address = contactDetail.getAddress();
    final String piCountry = country == null ? "" : country;
    final String languageCode = "Language Code";

    final PiRegister piRegister = customerTransformer.toPiRegister(customer, languageCode, country);

    assertThat(piRegister.getCountry(), is(piCountry));
    assertThat(piRegister.getLanguageCode(), is(languageCode));
    assertThat(piRegister.getAddress1(), is(address.getLine1()));
    assertThat(piRegister.getAddress2(), is(address.getLine2()));
    assertThat(piRegister.getAddress3(), is(address.getLine3()));
    assertThat(piRegister.getPostCode(), is(address.getPostCode()));
    assertThat(piRegister.getEmailAddress(), is(contactDetail.getEmail()));
    assertThat(piRegister.getUserName(), is(contactDetail.getEmail()));
    assertThat(piRegister.getGuestForename(), is(contactDetail.getFirstName()));
    assertThat(piRegister.getGuestSurname(), is(contactDetail.getLastName()));
    assertThat(piRegister.getGuestTitle(), is(contactDetail.getTitle()));
    assertThat(piRegister.getInfName(), is(contactDetail.getTitle() + " " + contactDetail.getLastName()));
    assertThat(piRegister.getTelephone(), is(contactDetail.getTelephone()));
  }

  @Test
  void toPiRegister_telephoneFallbackToMobile() {
    final Customer customer = random(Customer.class);
    final ContactDetail contactDetail = customer.getContactDetail();
    contactDetail.setTelephone(null);
    final String country = "Country name";
    final String languageCode = "Language Code";

    final PiRegister piRegister = customerTransformer.toPiRegister(customer, languageCode, country);

    assertThat(piRegister.getTelephone(), is(contactDetail.getMobile()));
  }
}