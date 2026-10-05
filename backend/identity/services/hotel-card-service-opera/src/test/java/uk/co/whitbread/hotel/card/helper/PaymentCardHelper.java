package uk.co.whitbread.hotel.card.helper;

import uk.co.whitbread.hotel.card.model.AddressDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardPIPersonal;
import uk.co.whitbread.shared.cdh.model.AdditionalGuest;
import uk.co.whitbread.shared.cdh.model.Address;
import uk.co.whitbread.shared.cdh.model.BookingPreference;
import uk.co.whitbread.shared.cdh.model.ContactDetail;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.Passport;
import uk.co.whitbread.shared.cdh.model.PaymentCard;
import uk.co.whitbread.shared.cdh.model.PaymentPreference;
import uk.co.whitbread.shared.cdh.model.RoomRequirements;

import java.util.List;

public class PaymentCardHelper {

    public static PaymentCardDTO buildPIPaymentCard() {
        var billingAddress = buildBillingAddress();
        return PaymentCardDTO.builder().business(false).personalCard(true).customerAccountId("CUST-123")
                .userEmail("user@email.com").cardToken("token123").cardNumberLast4Digits("1234")
                .expiryDate("12/28").cardType("VI").cardHolderName("Jack Smith").billingAddress(billingAddress)
                .cnpRequired(true).build();
    }

    public static AddressDTO buildBillingAddress() {
        return AddressDTO.builder().line1("line1 address")
                .postCode("4444 PP")
                .countryCode("GB")
                .country("England")
                .type("HOME")
                .companyName("Whitbread")
                .build();
    }

    public static PaymentCardPIPersonal buildPaymentCardPIPersonal(String customerAccountId, String userEmail){
        return PaymentCardPIPersonal.builder().customerAccountId(customerAccountId)
                .userEmail(userEmail).cardToken("token123").cardNumber("************1234")
                .expiryDate("12/29").cardType("MA").cardHolderName("Jack Smith").billingAddress(buildAddressDTO())
                .cnpRequired(true).build();
    }

    public static AddressDTO buildAddressDTO() {
        return AddressDTO.builder().line1("line1DTO").companyName("WB DTO").postCode("0000 PP").countryCode("DE")
                .country("GERMANY").type("HOME").build();
    }

    public static Address buildAddress() {
        return new Address("PCompany","GB","line1",
                "line2",null,null,null, "600400","HOME");
    }

    public static GetCustomerAccountResponse mockCdhResponse(String customerAccountId) {
        var response = new GetCustomerAccountResponse();
        response.setCustomerAccountId(customerAccountId);
        response.setBartGuestHistoryNumber("1234bart");
        response.setBartGuestHistoryCreation("12/22");
        response.setAdditionalGuests(List.of(new AdditionalGuest("11","guest@gmail.com","FirstName",
                "LastName","2223554331","33",new Passport("GB","321222222"), "55344645632",
                "title2")));
        response.setBookingPreference(new BookingPreference(2L, true,"reason",
                new RoomRequirements(1,1,false,"hotel","bok3","Type")));
        response.setContactDetail(new ContactDetail(new Address("CCompany","GB","line1",
                null,null,null,null, "5158 99","HOME"),
                "con1", "contact@gmail.com", "ContactFirst", "ContactLast", "323231232",
                "english", null, "1233221321","titleContact"));
        response.setPaymentPreference(new PaymentPreference(true, new PaymentCard(
                buildAddress(), "Jack Tin", "************3332","VI","12/27","token8764322")));
        return response;
    }

    public static PaymentCardDTO buildBBPaymentCardPersonal() {
        var billingAddress = buildBillingAddress();
        return PaymentCardDTO.builder().business(true).personalCard(true).employeeAccountId("EMPL-123")
                .companyAccountId("COMP-123").cnpBusinessAccountUsername("user").cnpBusinessAccountPassword("pass")
                .userEmail("user@email.com").cardToken("token123").cardNumberLast4Digits("1234")
                .expiryDate("12/28").cardType("VI").cardHolderName("Jack Smith").billingAddress(billingAddress)
                .cnpRequired(true).build();
    }

    public static PaymentCardDTO buildBBPaymentCardCentral() {
        var billingAddress = buildBillingAddress();
        return PaymentCardDTO.builder().business(true).personalCard(false).cardLabel("LABEL-123")
                .cnpBusinessAccountUsername("user").cnpBusinessAccountPassword("pass")
                .userEmail("user@email.com").cardToken("token123").cardNumberLast4Digits("1234")
                .expiryDate("12/28").cardType("VI").cardHolderName("Jack Smith").billingAddress(billingAddress)
                .cnpRequired(true).build();
    }
}
