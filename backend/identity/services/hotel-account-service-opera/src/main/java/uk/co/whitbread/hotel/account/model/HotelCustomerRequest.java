package uk.co.whitbread.hotel.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelCustomerRequest {

    String authorization;

    String customerId;

    HotelBrandCode hotelBrand;

    boolean business;

    BookingChannelCode bookingChannel;

}
