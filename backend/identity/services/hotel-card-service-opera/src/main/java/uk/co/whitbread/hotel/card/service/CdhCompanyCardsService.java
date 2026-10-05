package uk.co.whitbread.hotel.card.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.exceptions.CardNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CardMapper;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.utils.CardTokeniser;
import uk.co.whitbread.hotel.card.utils.PaymentCardMasker;
import uk.co.whitbread.shared.cdh.PaymentCardDataService;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;

@Service
@Slf4j
@AllArgsConstructor
public class CdhCompanyCardsService {

  private CardMapper cardMapper;
  private PaymentCardMasker paymentCardMasker;
  private PaymentCardDataService paymentCardDataService;
  private CardTokeniser cardTokeniser;

  public void updatePaymentCard(String companyId, String cardId, PaymentCard paymentCard,
      String accessedBy) {
    log.debug("Called CdhCompanyCardsService.updatePaymentCard");

    String maskedNumber =
        paymentCardMasker.isMasked(paymentCard.getCardNumber()) ? paymentCard.getCardNumber()
            : paymentCardMasker.maskNumber(paymentCard.getCardNumber());
    if (paymentCard.getCardNumber() != null) {
      cardTokeniser.tokeniseCard(paymentCard, accessedBy);
    }

    PaymentCardDetails paymentCardUpdateRequest = cardMapper
        .toPaymentCardDetails(paymentCard, maskedNumber);

    paymentCardDataService
        .updatePaymentCard(companyId, cardId, paymentCardUpdateRequest, accessedBy);
  }

  public void deletePaymentCard(String companyId, String cardId, String accessedBy) {
    log.debug("Called CdhCompanyCardsService.deletePaymentCard");
    paymentCardDataService.deletePaymentCard(companyId, cardId, accessedBy);
  }

  public GetPaymentCardDetailsResponse addCdhPaymentCard(String companyId, PaymentCard paymentCard,
      String accessedBy) {
    String maskedCardNumber = paymentCardMasker.maskNumber(paymentCard.getCardNumber());
    if (paymentCard.getCardNumber() != null) {
      cardTokeniser.tokeniseCard(paymentCard, accessedBy);
    }

    var paymentCardDetails = cardMapper.toPaymentCardDetails(paymentCard, maskedCardNumber);
    return paymentCardDataService.createCompanyPaymentCard(companyId, paymentCardDetails,
        accessedBy);
  }

  public List<PaymentCard> getPaymentCards(String companyId, String accessedBy) {
    List<GetPaymentCardDetailsResponse> paymentCardDetailsResponses = paymentCardDataService.getPaymentCards(
        companyId, accessedBy);

    return paymentCardDetailsResponses.stream()
        .map(paymentCardDetailsResponse -> cardMapper.toPaymentCard(paymentCardDetailsResponse))
        .collect(Collectors.toList());
  }
}
