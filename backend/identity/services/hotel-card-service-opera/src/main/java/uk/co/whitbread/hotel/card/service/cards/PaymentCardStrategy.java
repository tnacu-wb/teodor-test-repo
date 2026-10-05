package uk.co.whitbread.hotel.card.service.cards;

import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.PaymentCardCommon;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;

public interface PaymentCardStrategy {

    SaveCardPurpose strategyName();
    PaymentCardCommon mapPaymentCard(PaymentCardDTO paymentCardDTO);
    void updatePaymentCard(PaymentCardCommon paymentCard);

    @Autowired
    default void register(PaymentCardContext context) {
        context.registerStrategy(strategyName(), this);
    }

}
