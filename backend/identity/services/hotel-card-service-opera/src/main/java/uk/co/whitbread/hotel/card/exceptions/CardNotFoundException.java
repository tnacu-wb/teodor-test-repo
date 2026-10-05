package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

public class CardNotFoundException extends AbstractMALException implements MAL404HttpException {

  public static final String ERROR_CODE = "1102";
  public static final String CARD_NOT_FOUND_ERROR_MESSAGE = "No Payment card was found with cardId = ";

  public CardNotFoundException(String cardId) {
    super(CARD_NOT_FOUND_ERROR_MESSAGE + cardId);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
