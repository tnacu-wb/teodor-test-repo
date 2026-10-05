package uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AddressCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.BillingCcui;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.Address;
import uk.co.whitbread.basket.domain.model.payments.in.Billing;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.generated.models.ohip.BillingAddressCaptRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.BookerAddressDto;
import uk.co.whitbread.basket.generated.models.ohip.BookerDetailsDto;

@Mapper(componentModel = "spring")
public interface BillingAddressRequestOhipMapper {

  @Mapping(target = "hotelId", source = "payment.booking.businessSite.identifier")
  @Mapping(target = "reservationIds", source = "reservationIds")
  @Mapping(target = "booker", source = "payment.payment.billing")
  @Mapping(target = "channel", source = "payment.booking.channel")
  BillingAddressCaptRequestDto toDto(PaymentRequest payment,
                                     List<String> reservationIds,
                                     boolean updateGuestProfile,
                                     boolean updateCompanyProfile,
                                     boolean updateContactProfile);

  @Mapping(target = "hotelId", source = "ccuiPaymentRequest.paymentRequest.booking.businessSite.identifier")
  @Mapping(target = "reservationIds", source = "reservationIds")
  @Mapping(target = "booker", source = "ccuiPaymentRequest.paymentRequest.payment.billing")
  @Mapping(target = "paymentOption", source = "ccuiPaymentRequest.paymentOption")
  BillingAddressCaptRequestDto toDto(CcuiPaymentRequest ccuiPaymentRequest, List<String> reservationIds);

  @Mapping(target = "emailAddress", source = "billing.email")
  @Mapping(target = "landline", source = "billing.telephone")
  BookerDetailsDto toDto(Billing billing);

  @Mapping(target = "emailAddress", source = "billing.email")
  @Mapping(target = "landline", source = "billing.telephone")
  BookerDetailsDto toDto(BillingCcui billing);

  @Mapping(target = "addressLine1", source = "address.line1")
  @Mapping(target = "addressLine2", source = "address.line2")
  @Mapping(target = "addressLine3", source = "address.line3")
  @Mapping(target = "addressLine4", source = "address.line4")
  BookerAddressDto toDto(Address address);

  @Mapping(target = "addressLine1", source = "address.line1")
  @Mapping(target = "addressLine2", source = "address.line2")
  @Mapping(target = "addressLine3", source = "address.line3")
  @Mapping(target = "addressLine4", source = "address.line4")
  BookerAddressDto toDto(AddressCcui address);
}
