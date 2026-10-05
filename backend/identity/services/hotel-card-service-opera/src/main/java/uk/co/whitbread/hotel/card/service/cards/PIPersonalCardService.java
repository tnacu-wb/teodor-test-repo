package uk.co.whitbread.hotel.card.service.cards;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CDHMapper;
import uk.co.whitbread.hotel.card.mapper.PaymentCardMapper;
import uk.co.whitbread.hotel.card.model.PaymentCardCommon;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardPIPersonal;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PIPersonalCardService implements PaymentCardStrategy {

    private final CustomerDataService customerDataService;
    private final CDHMapper cdhMapper;
    private final PaymentCardMapper paymentCardMapper;
    private final Validator validator;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    @Override
    public SaveCardPurpose strategyName() {
        return SaveCardPurpose.PI_PERSONAL;
    }

    @Override
    public PaymentCardCommon mapPaymentCard(PaymentCardDTO paymentCardDTO) {
        return paymentCardMapper.toPaymentCardPIPersonal(paymentCardDTO);
    }

    @Override
    public void updatePaymentCard(PaymentCardCommon paymentCard) {
        log.debug("Called PIPersonalCardService.updatePaymentCard");

        PaymentCardPIPersonal card = (PaymentCardPIPersonal) paymentCard;
        validator.validate(card);
        Optional<GetCustomerAccountResponse> customerAccountOptional =
                getCustomerAccount(card.getCustomerAccountId(), card.getUserEmail());
        if (customerAccountOptional.isEmpty()) {
            throw new AccountNotFoundException(
                    String.format("Account with id %s was not found", card.getCustomerAccountId()));
        }
        CustomerAccountRequest cardUpdateForAccount =
                cdhMapper.toCustomerAccountRequest(customerAccountOptional.get(), card);
        customerDataService.updateCustomerAccount(card.getCustomerAccountId(), cardUpdateForAccount, card.getUserEmail());
    }

    private Optional<GetCustomerAccountResponse> getCustomerAccount(String customerAccountId,
        String accessedBy) {
        if (isCdhApiDeprecationEnabled()) {
            return customerDataService.getCustomerAccountV3(customerAccountId, accessedBy);
        }
        return customerDataService.getCustomerAccount(customerAccountId, accessedBy);
    }

    private boolean isCdhApiDeprecationEnabled() {
        return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
    }

}
