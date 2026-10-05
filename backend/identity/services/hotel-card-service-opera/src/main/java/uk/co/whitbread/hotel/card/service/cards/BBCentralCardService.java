package uk.co.whitbread.hotel.card.service.cards;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.exceptions.CardNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CDHMapper;
import uk.co.whitbread.hotel.card.mapper.PaymentCardMapper;
import uk.co.whitbread.hotel.card.model.*;
import uk.co.whitbread.shared.cdh.PaymentCardDataService;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;

@Service
@Slf4j
@RequiredArgsConstructor
public class BBCentralCardService implements PaymentCardStrategy {
    private final PaymentCardMapper paymentCardMapper;
    private final CDHMapper cdhMapper;
    private final PaymentCardDataService paymentCardDataService;
    private final Validator validator;

    @Override
    public SaveCardPurpose strategyName() {
        return SaveCardPurpose.BB_CENTRAL;
    }

    @Override
    public PaymentCardCommon mapPaymentCard(PaymentCardDTO paymentCardDTO) {
        return paymentCardMapper.toPaymentCardBBCentral(paymentCardDTO);
    }

    @Override
    public void updatePaymentCard(PaymentCardCommon paymentCard) {
        log.debug("Called BBCentralCardService.updatePaymentCard");

        PaymentCardBBCentral card = (PaymentCardBBCentral) paymentCard;
        validator.validate(card);
        if (StringUtils.isBlank(card.getCardId())) {
            addNewCentrallyStoredCard(card);
        } else {
            editCentrallyStoredCard(card);
        }
    }

    private void addNewCentrallyStoredCard(PaymentCardBBCentral card) {
        log.debug("Called BBCentralCardService.addNewCentrallyStoredCard");
        PaymentCardDetails paymentCardDetails = cdhMapper.toPaymentCardDetails(card);
        paymentCardDataService.createCompanyPaymentCard(card.getCompanyId(), paymentCardDetails, card.getUserEmail());
    }

    private void editCentrallyStoredCard(PaymentCardBBCentral card) {
        log.debug("Called BBCentralCardService.editCentrallyStoredCard for cardId {}", card.getCardId());
        PaymentCardDetails paymentCardDetails = getUpdatedPaymentCardDetails(card);
        paymentCardDataService
                .updatePaymentCard(card.getCompanyId(), card.getCardId(), paymentCardDetails, card.getUserEmail());
    }

    private PaymentCardDetails getUpdatedPaymentCardDetails(PaymentCardBBCentral card) {
        if (!CardType.NONE.name().equalsIgnoreCase(card.getCardType())) {
            return cdhMapper.toPaymentCardDetails(card);
        }
        return paymentCardDataService.getPaymentCards(card.getCompanyId(), card.getUserEmail())
                .stream()
                .filter(paymentCard -> card.getCardId().equalsIgnoreCase(paymentCard.getCardId()))
                .findFirst()
                .map(paymentCard -> cdhMapper.updateWithCdhDetails(cdhMapper.toPaymentCardDetails(card), paymentCard))
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
    }
}
