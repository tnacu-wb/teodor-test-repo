package uk.co.whitbread.hotel.register.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.hotel.register.mapper.CustomerMapper;
import uk.co.whitbread.hotel.register.model.*;
import uk.co.whitbread.shared.cdh.model.AdditionalGuest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.RoomRequirements;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CustomerMapperTest {

  private CustomerMapper converter;

  @BeforeEach
  public void setUp() {
    converter = Mappers.getMapper(CustomerMapper.class);
  }

  @Test
  public void testConvertContactDetail_convertWithGHNParams() {
    final Address address = buildAddress();
    final Passport passport = buildPassport();
    final ContactDetail contactDetail = buildContactDetail(address, passport, "");
    final Customer customer = new Customer();
    customer.setContactDetail(contactDetail);

    final CustomerAccountRequest customerAccountRequest =
        converter.toCdhRequest(customer, "ghn", LocalDate.now());

    final uk.co.whitbread.shared.cdh.model.ContactDetail cdhDetail =
        customerAccountRequest.getContactDetail();
    final uk.co.whitbread.shared.cdh.model.Address cdhAddress = cdhDetail.getAddress();
    final Passport cdhPassport = contactDetail.getPassport();

    assertThat(cdhDetail.getEmail(), is(contactDetail.getEmail()));
    assertThat(cdhDetail.getCarRegistration(), is(contactDetail.getCarRegistration()));
    assertThat(cdhDetail.getFirstName(), is(contactDetail.getFirstName()));
    assertThat(cdhDetail.getLastName(), is(contactDetail.getLastName()));
    assertThat(cdhDetail.getMobile(), is(contactDetail.getMobile()));
    assertThat(cdhDetail.getTelephone(), is(contactDetail.getTelephone()));
    assertThat(cdhDetail.getTitle(), is(contactDetail.getTitle()));
    assertThat(cdhDetail.getNationality(), is(contactDetail.getNationality()));

    assertThat(cdhAddress.getCompanyName(), is(address.getCompanyName()));
    assertThat(cdhAddress.getCountry(), is(address.getCountryCode()));
    assertThat(cdhAddress.getAddressLine1(), is(address.getLine1()));
    assertThat(cdhAddress.getAddressLine2(), is(address.getLine2()));
    assertThat(cdhAddress.getAddressLine3(), is(address.getLine3()));
    assertThat(cdhAddress.getAddressLine4(), is(address.getLine4()));
    assertThat(cdhAddress.getAddressLine5(), is(address.getLine5()));
    assertThat(cdhAddress.getPostCode(), is(address.getPostCode()));
    assertThat(cdhAddress.getType(), is(address.getType().name()));

    assertThat(cdhPassport.getNumber(), is(passport.getNumber()));
    assertThat(cdhPassport.getCountryOfIssue(), is(passport.getCountryOfIssue()));
  }

  @Test
  public void testConvertContactDetail_convertWithNoGHNParams() {
    final Address address = buildAddress();
    final Passport passport = buildPassport();
    final ContactDetail contactDetail = buildContactDetail(address, passport, "");
    final Customer customer = new Customer();
    customer.setContactDetail(contactDetail);

    final CustomerAccountRequest customerAccountRequest =
        converter.toCdhRequest(customer);

    final uk.co.whitbread.shared.cdh.model.ContactDetail cdhDetail =
        customerAccountRequest.getContactDetail();
    final uk.co.whitbread.shared.cdh.model.Address cdhAddress = cdhDetail.getAddress();
    final Passport cdhPassport = contactDetail.getPassport();

    assertThat(cdhDetail.getEmail(), is(contactDetail.getEmail()));
    assertThat(cdhDetail.getCarRegistration(), is(contactDetail.getCarRegistration()));
    assertThat(cdhDetail.getFirstName(), is(contactDetail.getFirstName()));
    assertThat(cdhDetail.getLastName(), is(contactDetail.getLastName()));
    assertThat(cdhDetail.getMobile(), is(contactDetail.getMobile()));
    assertThat(cdhDetail.getTelephone(), is(contactDetail.getTelephone()));
    assertThat(cdhDetail.getTitle(), is(contactDetail.getTitle()));
    assertThat(cdhDetail.getNationality(), is(contactDetail.getNationality()));

    assertThat(cdhAddress.getCompanyName(), is(address.getCompanyName()));
    assertThat(cdhAddress.getCountry(), is(address.getCountryCode()));
    assertThat(cdhAddress.getAddressLine1(), is(address.getLine1()));
    assertThat(cdhAddress.getAddressLine2(), is(address.getLine2()));
    assertThat(cdhAddress.getAddressLine3(), is(address.getLine3()));
    assertThat(cdhAddress.getAddressLine4(), is(address.getLine4()));
    assertThat(cdhAddress.getAddressLine5(), is(address.getLine5()));
    assertThat(cdhAddress.getPostCode(), is(address.getPostCode()));
    assertThat(cdhAddress.getType(), is(address.getType().name()));

    assertThat(cdhPassport.getNumber(), is(passport.getNumber()));
    assertThat(cdhPassport.getCountryOfIssue(), is(passport.getCountryOfIssue()));
  }

  @Test
  public void testConvertAdditionalGuests() {
    ContactDetail contactDetail1 = buildContactDetail(new Address(), new Passport(), "1");
    ContactDetail contactDetail2 = buildContactDetail(new Address(), new Passport(), "2");
    ContactDetail contactDetail3 = buildContactDetail(new Address(), new Passport(), "3");

    Customer customer = new Customer();
    customer.setAdditionalGuests(Arrays.asList(contactDetail1, contactDetail2, contactDetail3));

    final CustomerAccountRequest customerAccountRequest =
        converter.toCdhRequest(customer, "ghn", LocalDate.now());

    final List<AdditionalGuest> cdhGuests = customerAccountRequest.getAdditionalGuests();

    assertThat(cdhGuests, hasSize(3));

    final Map<String, AdditionalGuest> cdhGuestsMap = cdhGuests.stream()
        .collect(Collectors.toMap(AdditionalGuest::getFirstName, g -> g));

    final AdditionalGuest cdhGuest1 = cdhGuestsMap.get(contactDetail1.getFirstName());

    assertThat(cdhGuest1, notNullValue());
    assertThat(cdhGuestsMap.get(contactDetail2.getFirstName()), notNullValue());
    assertThat(cdhGuestsMap.get(contactDetail3.getFirstName()), notNullValue());

    assertThat(cdhGuest1.getEmail(), is(contactDetail1.getEmail()));
    assertThat(cdhGuest1.getCarRegistration(), is(contactDetail1.getCarRegistration()));
    assertThat(cdhGuest1.getFirstName(), is(contactDetail1.getFirstName()));
    assertThat(cdhGuest1.getLastName(), is(contactDetail1.getLastName()));
    assertThat(cdhGuest1.getMobile(), is(contactDetail1.getMobile()));
    assertThat(cdhGuest1.getTelephone(), is(contactDetail1.getTelephone()));
    assertThat(cdhGuest1.getTitle(), is(contactDetail1.getTitle()));
    assertThat(cdhGuest1.getNationality(), is(contactDetail1.getNationality()));
  }

  @Test
  public void testConvertBookingPreference() {
    final RoomCriteria roomCriteria = buildRoomCriteria();
    final BookingPreference bookingPreference = buildBookingPreference(roomCriteria);
    final Customer customer = new Customer();
    customer.setBookingPreference(bookingPreference);

    final CustomerAccountRequest customerAccountRequest =
        converter.toCdhRequest(customer, "ghn", LocalDate.now());

    final uk.co.whitbread.shared.cdh.model.BookingPreference cdhPreference =
        customerAccountRequest.getBookingPreference();
    final RoomRequirements cdhRequirements = cdhPreference.getRoomRequirements();

    assertThat(cdhPreference.getReason(), is(bookingPreference.getReason().name()));
    assertThat(cdhPreference.getFoodPreference(), is(bookingPreference.getFoodPreference()));
    assertThat(cdhRequirements.getType(), is(roomCriteria.getType().name()));
    assertThat(cdhRequirements.getAdults(), is(roomCriteria.getAdults()));
    assertThat(cdhRequirements.getChildren(), is(roomCriteria.getChildren()));
    assertThat(cdhRequirements.getHotelBrand(), is(roomCriteria.getHotelBrand()));
    assertThat(cdhRequirements.getLettingType(), is(roomCriteria.getLettingType()));
  }

  @Test
  public void testConvertPaymentPreference() {
    final PaymentCard paymentCard = buildPaymentCard();
    final PaymentPreference paymentPreference = buildPaymentPreference(paymentCard);
    final Customer customer = new Customer();
    customer.setPaymentPreference(paymentPreference);

    final CustomerAccountRequest customerAccountRequest =
        converter.toCdhRequest(customer, "ghn", LocalDate.now());

    final uk.co.whitbread.shared.cdh.model.PaymentPreference cdhPreference =
        customerAccountRequest.getPaymentPreference();
    final uk.co.whitbread.shared.cdh.model.PaymentCard cdhCard = cdhPreference.getPaymentCard();

    assertThat(cdhPreference.getElectronicInvoiceRequired(),
        is(paymentPreference.isElectronicInvoiceRequired()));
    assertThat(cdhCard.getCardType(), is(paymentCard.getCardType()));
    assertThat(cdhCard.getCardNumber(), is(paymentCard.getCardNumber()));
    assertThat(cdhCard.getExpiryDate(), is(paymentCard.getExpiryDate()));
    assertThat(cdhCard.getCardHolderName(), is(paymentCard.getCardHolderName()));
  }

  @Test
  public void testConvertSimpleFields() {
    final Customer customer = new Customer();

    final String guestHistoryNumber = "ghn";
    final LocalDate guestHistoryCreation = LocalDate.now();

    final CustomerAccountRequest customerAccountRequest =
        converter.toCdhRequest(customer, guestHistoryNumber, guestHistoryCreation);

    assertThat(customerAccountRequest.getBartGuestHistoryNumber(), is(guestHistoryNumber));
    assertThat(customerAccountRequest.getBartGuestHistoryCreation(), is(guestHistoryCreation.toString()));
  }

  private PaymentPreference buildPaymentPreference(PaymentCard paymentCard) {
    final PaymentPreference paymentPreference = new PaymentPreference();
    paymentPreference.setPaymentCard(paymentCard);
    paymentPreference.setElectronicInvoiceRequired(true);
    return paymentPreference;
  }

  private PaymentCard buildPaymentCard() {
    final PaymentCard paymentCard = new PaymentCard();
    paymentCard.setCardType("cardType");
    paymentCard.setCardType("cardNumber");
    paymentCard.setStartDate("startDate");
    paymentCard.setExpiryDate("expiryDate");
    paymentCard.setIssueNumber("issueNumber");
    paymentCard.setCardHolderName("cardHolderName");
    return paymentCard;
  }

  private BookingPreference buildBookingPreference(RoomCriteria roomCriteria) {
    final BookingPreference bookingPreference = new BookingPreference();
    bookingPreference.setFoodPreference(12L);
    bookingPreference.setWantSmsConfirmations(true);
    bookingPreference.setReason(BookingType.LEISURE);
    bookingPreference.setRoomRequirements(roomCriteria);
    return bookingPreference;
  }

  private RoomCriteria buildRoomCriteria() {
    final RoomCriteria roomCriteria = new RoomCriteria();
    roomCriteria.setType(RoomType.SB);
    roomCriteria.setLettingType("lettingType");
    roomCriteria.setAdults(2);
    roomCriteria.setChildren(3);
    roomCriteria.setCotRequired(true);
    roomCriteria.setHotelBrand("hotelBrand");
    return roomCriteria;
  }

  private ContactDetail buildContactDetail(Address address, Passport passport, String firstNameSuffix) {
    final ContactDetail contactDetail = new ContactDetail();
    contactDetail.setAddress(address);
    contactDetail.setPassport(passport);
    contactDetail.setDialingCode("dialingCode");
    contactDetail.setCarRegistration("carRegistration");
    contactDetail.setEmail("email@email.com");
    contactDetail.setFirstName("firstName_" + firstNameSuffix);
    contactDetail.setLastName("lastName");
    contactDetail.setMobile("1234567890");
    contactDetail.setTelephone("0987654321");
    contactDetail.setNationality("nationality");
    contactDetail.setTitle("title");
    return contactDetail;
  }

  private Passport buildPassport() {
    final Passport passport = new Passport();
    passport.setNumber("number");
    passport.setCountryOfIssue("countryOfIssue");
    return passport;
  }

  private Address buildAddress() {
    final Address address = new Address();
    address.setCompanyName("address.companyName");
    address.setCountryCode("countryCode");
    address.setLine1("line1");
    address.setLine2("line2");
    address.setLine3("line3");
    address.setLine4("line4");
    address.setLine5("line5");
    address.setPostCode("postCode");
    address.setType(AddressType.BUSINESS);
    return address;
  }
}