package uk.co.whitbread.hotel.register.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.register.exceptions.BusinessValidationException;
import uk.co.whitbread.hotel.register.model.Customer;

@Component
public class BusinessCustomerValidator {

    public void validate(Customer payload){

        if (StringUtils.isBlank(payload.getContactDetail().getTelephone())){
        throw new BusinessValidationException("telephone may not be empty");
        }

        if (StringUtils.isBlank(payload.getAlternativeName())){
            throw new BusinessValidationException("alternativeName may not be empty");
        }
    }
}
