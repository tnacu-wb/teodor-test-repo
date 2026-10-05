package uk.co.whitbread.hotel.account.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static uk.co.whitbread.hotel.account.utils.EnumConverter.getEnum;

import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.co.whitbread.hotel.account.model.Address;
import uk.co.whitbread.hotel.account.model.BillingAddress;
import uk.co.whitbread.hotel.account.model.BookingType;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.CustomerResponse;
import uk.co.whitbread.hotel.account.model.HotelBrandCode;
import uk.co.whitbread.hotel.account.model.SearchCustomerRequest;
import uk.co.whitbread.hotel.account.utils.account.converter.StringToLocalDate;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.SearchCustomerAccountRequest;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE,
        uses = {AddressMapper.class, AddressTypeMapper.class, CardNumberMapper.class},
        imports = {StringToLocalDate.class})
public interface CustomerMapper {

    @Mapping(target = "bartGuestHistoryNumber", source = "guestHistoryNumber")
    @Mapping(target = "bartGuestHistoryCreation", source = "guestHistoryCreation")
    @Mapping(target = "paymentPreference.paymentCard.cardNumber",
            source = "paymentPreference.paymentCard.cardNumber", qualifiedByName = "maskCardNumber")
    @Mapping(target = "paymentPreference.paymentCard.token", source = "paymentPreference.paymentCard.cardToken")
    CustomerAccountRequest toCustomerAccountRequest(Customer customer);

    @Mapping(target = "customerId", source = "customerAccountId")
    @Mapping(target = "success", constant = "true")
    CustomerResponse toCustomerResponse(CustomerAccountResponse response);

    @Mapping(target = "title", source = "contactDetail.title")
    @Mapping(target = "firstName", source = "contactDetail.firstName")
    @Mapping(target = "lastName", source = "contactDetail.lastName")
    @Mapping(target = "telephone", source = "contactDetail.telephone")
    @Mapping(target = "mobile", source = "contactDetail.mobile")
    @Mapping(target = "email", source = "contactDetail.email")
    @Mapping(target = "addressLine1", source = "contactDetail.address.addressLine1")
    @Mapping(target = "addressLine2", source = "contactDetail.address.addressLine2")
    @Mapping(target = "addressLine3", source = "contactDetail.address.addressLine3")
    @Mapping(target = "countryCode", source = "contactDetail.address.country")
    @Mapping(target = "postCode", source = "contactDetail.address.postCode")
    AccountUpdate toAccountUpdate(CustomerAccountRequest updatedAccount);

    Customer updateCustomerFromCustomerRequest(CustomerRequest customerRequest, @MappingTarget Customer customer);

    @Mapping(source = "bartGuestHistoryNumber", target = "guestHistoryNumber")
    @Mapping(source = "bartGuestHistoryCreation", target = "guestHistoryCreation")
    @Mapping(
            expression = "java( getHotelBrandCode(roomRequirements.getHotelBrand()) )",
            target = "bookingPreference.roomRequirements.hotelBrand")
    @Mapping(
            expression = "java( getBookingType(bookingPreference.getReason()) )",
            target = "bookingPreference.reason")
    @Mapping(target = "paymentPreference.paymentCard.cardToken", source = "paymentPreference.paymentCard.token")
    Customer toCustomer(GetCustomerAccountResponse getCustomerAccountResponse);

    @Mapping(target = "line1", source = "addressLine1")
    @Mapping(target = "line2", source = "addressLine2")
    @Mapping(target = "line3", source = "addressLine3")
    @Mapping(target = "line4", source = "addressLine4")
    @Mapping(target = "line5", source = "addressLine5")
    @Mapping(target = "countryCodeISO", source = "country")
    BillingAddress toBillingAddress(uk.co.whitbread.shared.cdh.model.Address address);

    List<Customer> toListOfCustomer(List<GetCustomerAccountResponse> getCustomerAccountResponse);

    SearchCustomerAccountRequest toSearchCustomerAccountRequest(SearchCustomerRequest getCustomerAccountResponse);

    @Mapping(target = "addressLine1", source = "line1")
    @Mapping(target = "addressLine2", source = "line2")
    @Mapping(target = "addressLine3", source = "line3")
    @Mapping(target = "addressLine4", source = "line4")
    @Mapping(target = "addressLine5", source = "line5")
    @Mapping(target = "country", source = "countryCode")
    uk.co.whitbread.shared.cdh.model.Address toCdhAddress(Address address);

    @InheritInverseConfiguration
    Address toAddress(uk.co.whitbread.shared.cdh.model.Address address);

    default HotelBrandCode getHotelBrandCode(String brandCode) {
        return getEnum(HotelBrandCode.class, brandCode);
    }

    default BookingType getBookingType(String bookingType) {
        return getEnum(BookingType.class, bookingType);
    }
}
