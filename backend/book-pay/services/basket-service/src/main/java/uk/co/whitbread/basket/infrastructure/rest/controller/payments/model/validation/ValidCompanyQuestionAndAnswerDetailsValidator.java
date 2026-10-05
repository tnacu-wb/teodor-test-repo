package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDto;

public class ValidCompanyQuestionAndAnswerDetailsValidator implements
    ConstraintValidator<ValidCompanyQuestionAndAnswerDetails, CompanyQuestionAndAnswerDetailsDto> {
  private static final String CUSTOMER_AND_PO_REFERENCE_PATTERN = "^([a-zA-Z0-9\\s.-]){0,24}$";

  private static final String USER_DEFINED_PATTERN = "^([A-Za-zÀ-ÖØ-öø-ÿ0-9.,;:&()_?!\\-\\s\"'~#*/]){0,50}$";

  private static final String INVALID_ANSWER = "Answer is not as per the standards";

  @Override
  public void initialize(ValidCompanyQuestionAndAnswerDetails constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetailsDto,
      ConstraintValidatorContext context) {

    List<Boolean> isValid = new ArrayList<>();

    if (companyQuestionAndAnswerDetailsDto != null)  {
      isValidCompanyQuestionAndAnswers(CUSTOMER_AND_PO_REFERENCE_PATTERN, context, isValid,
          companyQuestionAndAnswerDetailsDto.getPurchaseOrderQuestionAndAnswer(),
          companyQuestionAndAnswerDetailsDto.getCustomerReferenceQuestionAndAnswer());

      if (CollectionUtils.isNotEmpty(companyQuestionAndAnswerDetailsDto.getUserDefinedQuestionAndAnswers())) {
        companyQuestionAndAnswerDetailsDto.getUserDefinedQuestionAndAnswers()
            .forEach(qna -> isValidCompanyQuestionAndAnswers(USER_DEFINED_PATTERN, context, isValid, qna));
      }
    }

    // Empty means no issues found
    return isValid.isEmpty();
  }

  private void isValidCompanyQuestionAndAnswers(final String regex, final ConstraintValidatorContext context,
      final List<Boolean> isValid, final CompanyQuestionAndAnswerDto... companyQuestionAndAnswers) {
    if (companyQuestionAndAnswers != null) {
      for (CompanyQuestionAndAnswerDto companyQuestionAndAnswer : companyQuestionAndAnswers) {
        if (companyQuestionAndAnswer != null && companyQuestionAndAnswer.getAnswer() != null
            && !Pattern.compile(regex).matcher(companyQuestionAndAnswer.getAnswer()).matches()) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate(companyQuestionAndAnswer.getAnswer() + ":" + INVALID_ANSWER)
              .addConstraintViolation();
          isValid.add(false);
        }
      }
    }
  }

}
