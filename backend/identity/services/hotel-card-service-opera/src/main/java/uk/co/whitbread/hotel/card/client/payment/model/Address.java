package uk.co.whitbread.hotel.card.client.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String line1;
    private String line2;
    private String line3;
    private String line4;
    private String countryCode = "GB";
    private String postalCode;
}