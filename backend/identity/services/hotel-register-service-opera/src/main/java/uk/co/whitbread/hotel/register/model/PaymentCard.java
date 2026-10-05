package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCard {
    private String cardType;
    private String cardNumber;
    private String startDate;
    private String expiryDate;
    private String issueNumber;
    private String cardHolderName;
}