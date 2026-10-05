package uk.co.whitbread.hotel.account.validation;

import static org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit.LUHN_CHECK_DIGIT;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import uk.co.whitbread.hotel.account.model.PaymentCard;

@RequiredArgsConstructor
public class PaymentDetailsValidator implements ConstraintValidator<PaymentDetails, PaymentCard> {

  private static final String MASKED_CARD_NUMBER_PATTERN = "^[*]{8,15}[\\d]{4}$";
  private static final String CARD_NUMBER_PATTERN = "^[\\d]{12,19}$";
  private static final String CARD_TYPE_PATTERN = "^[A-Z]{2}$";
  private static final String EXPIRY_DATE_PATTERN = "^(0[1-9]|1[0-2])/?[\\d]{2}$";
  private static final String INVALID_CARD_NUMBER_MESSAGE = "Invalid card number";
  private static final String INVALID_CARD_TYPE_MESSAGE = "Invalid card type";
  private static final String INVALID_EXPIRY_DATE_MESSAGE = "Invalid expiry date";
  private static final String BUSINESS_ACCOUNT_PW_REQUIRED_MESSAGE = "Business Account Password required";

  @Override
  public void initialize(PaymentDetails paymentDetails) {
    // No initialization required
  }

  @Override
  public boolean isValid(PaymentCard paymentCard,
      ConstraintValidatorContext constraintValidatorContext) {
    return isPaymentEmpty(paymentCard)
        || isCardNumberValid(paymentCard.getCardNumber(), constraintValidatorContext)
        && isExpiryDateValid(paymentCard.getExpiryDate(), constraintValidatorContext)
        && isCardTypeValid(paymentCard.getCardType(), constraintValidatorContext)
        && isCnpBusinessAccountPasswordValid(paymentCard.getCnpRequired(),
        paymentCard.getCnpBusinessAccountPassword(), constraintValidatorContext)
        && StringUtils.hasLength(paymentCard.getCardHolderName());
  }


  public boolean isPaymentEmpty(PaymentCard paymentCard) {
    return Optional.ofNullable(paymentCard).isEmpty()
        || Optional.ofNullable(paymentCard.getCardNumber()).isEmpty()
        && Optional.ofNullable(paymentCard.getCardType()).isEmpty()
        && Optional.ofNullable(paymentCard.getCardHolderName()).isEmpty()
        && Optional.ofNullable(paymentCard.getExpiryDate()).isEmpty()
        && Optional.ofNullable(paymentCard.getBillingAddress()).isEmpty()
        && Optional.ofNullable(paymentCard.getCnpBusinessAccountPassword()).isEmpty()
        && Optional.ofNullable(paymentCard.getCnpBusinessAccountUsername()).isEmpty()
        && Optional.ofNullable(paymentCard.getIssueNumber()).isEmpty()
        && Optional.ofNullable(paymentCard.getStartDate()).isEmpty();
  }

  private boolean isCardNumberValid(String cardNumber, ConstraintValidatorContext context) {
    if (Objects.isNull(cardNumber)) {
      setValidationFailedMessage(context, INVALID_CARD_NUMBER_MESSAGE);
      return false;
    }
    if (cardNumber.matches(MASKED_CARD_NUMBER_PATTERN)) {
      return true;
    }
    if (!cardNumber.matches(CARD_NUMBER_PATTERN) || !LUHN_CHECK_DIGIT.isValid(cardNumber)) {
      setValidationFailedMessage(context, INVALID_CARD_NUMBER_MESSAGE);
      return false;
    }
    return true;
  }

  private boolean isCardTypeValid(String cardType, ConstraintValidatorContext context) {
    if (Objects.isNull(cardType) || !cardType.matches(CARD_TYPE_PATTERN)) {
      setValidationFailedMessage(context, INVALID_CARD_TYPE_MESSAGE);
      return false;
    }
    return true;
  }

  private boolean isExpiryDateValid(String expiryDate, ConstraintValidatorContext context) {
    if (Objects.isNull(expiryDate) || !expiryDate.matches(EXPIRY_DATE_PATTERN)) {
      setValidationFailedMessage(context, INVALID_EXPIRY_DATE_MESSAGE);
      return false;
    }
    var formattedExpiryDate = expiryDate.replace("/", "");
    var formatter = DateTimeFormatter.ofPattern("MMyy");
    if (!YearMonth.parse(formattedExpiryDate, formatter).isBefore(YearMonth.now())) {
      return true;
    }
    setValidationFailedMessage(context, INVALID_EXPIRY_DATE_MESSAGE);
    return false;
  }

  private boolean isCnpBusinessAccountPasswordValid(Boolean cnpRequired,
      String cnpBusinessAccountPassword, ConstraintValidatorContext context) {
    if (Objects.isNull(cnpRequired)) {
      return true;
    }
    if (Boolean.FALSE.equals(cnpRequired) || StringUtils.hasLength(cnpBusinessAccountPassword)) {
      return true;
    }
    setValidationFailedMessage(context, BUSINESS_ACCOUNT_PW_REQUIRED_MESSAGE);
    return false;
  }

  private void setValidationFailedMessage(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}
