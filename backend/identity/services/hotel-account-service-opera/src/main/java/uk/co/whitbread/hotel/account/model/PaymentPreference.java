package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "paymentCard")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean electronicInvoiceRequired;

    @Valid
    private PaymentCard paymentCard;
}
