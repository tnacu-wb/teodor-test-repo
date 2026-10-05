package uk.co.whitbread.company.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaymentDetails {
    private boolean profileLocked;
    private boolean allowIndividualCards;
    private List<PaymentCard> paymentCards;
}
