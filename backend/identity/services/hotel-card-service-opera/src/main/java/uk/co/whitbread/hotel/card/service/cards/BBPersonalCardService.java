package uk.co.whitbread.hotel.card.service.cards;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CDHMapper;
import uk.co.whitbread.hotel.card.mapper.PaymentCardMapper;
import uk.co.whitbread.hotel.card.model.PaymentCardBBPersonal;
import uk.co.whitbread.hotel.card.model.PaymentCardCommon;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BBPersonalCardService implements PaymentCardStrategy {
    private final PaymentCardMapper paymentCardMapper;
    private final EmployeeDataService employeeDataService;
    private final CDHMapper cdhMapper;
    private final Validator validator;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    @Override
    public SaveCardPurpose strategyName() {
        return SaveCardPurpose.BB_PERSONAL;
    }

    @Override
    public PaymentCardCommon mapPaymentCard(PaymentCardDTO paymentCardDTO) {
        return paymentCardMapper.toPaymentCardBBPersonal(paymentCardDTO);
    }

    @Override
    public void updatePaymentCard(PaymentCardCommon paymentCard) {
        log.debug("Called BBPersonalCardService.updatePaymentCard");

        var card = (PaymentCardBBPersonal) paymentCard;
        validator.validate(card);
        var employeeOptional = getEmployeeFromCdh(card.getCompanyAccountId(), card.getEmployeeAccountId(),
                card.getUserEmail());
        var response = employeeOptional.orElseThrow(() -> new AccountNotFoundException(
                String.format("Employee %s from company %s was not found", card.getEmployeeAccountId(),
                        card.getCompanyAccountId())));
        EmployeeAccountRequest employee = cdhMapper.toEmployeeAccountRequest(response);
        var cardUpdateForEmployee = cdhMapper.updateWithCardDetails(card, employee);
        employeeDataService.updateEmployeeAccount(card.getCompanyAccountId(), card.getEmployeeAccountId(),
                cardUpdateForEmployee, card.getUserEmail());
    }

    private Optional<GetEmployeeResponse> getEmployeeFromCdh(String companyAccountId,
                                                             String employeeAccountId, String accessedBy) {
        if (isCdhApiDeprecationEnabled()) {
            return employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, accessedBy);
        }
        return employeeDataService.getEmployee(companyAccountId, employeeAccountId, accessedBy);
    }

    private boolean isCdhApiDeprecationEnabled() {
        return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
    }
}
