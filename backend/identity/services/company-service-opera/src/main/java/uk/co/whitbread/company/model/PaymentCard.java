package uk.co.whitbread.company.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentCard {
    private String cardId;
    private String cardLabel;
    private String cardType;
    private String nameOnCard;
    private String cardNumber;
    private String startDate;
    private String expiryDate;
    private String issueNumber;
    private String cardToken;
    private Address billingAddress;
    private boolean cardNotPresentRequired;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CardNotPresent cardNotPresent;

    public PaymentCard(String cardId, String cardLabel, String cardType, String nameOnCard, String cardNumber,
        String expiryDate, Address billingAddress) {
        this.cardId = cardId;
        this.cardLabel = cardLabel;
        this.cardType = cardType;
        this.nameOnCard = nameOnCard;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.billingAddress = billingAddress;
    }
}
