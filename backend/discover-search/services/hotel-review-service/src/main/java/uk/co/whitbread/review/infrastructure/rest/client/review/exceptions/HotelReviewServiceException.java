package uk.co.whitbread.review.infrastructure.rest.client.review.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelReviewServiceException extends AbstractInternalException {

  public HotelReviewServiceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}