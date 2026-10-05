package uk.co.whitbread.common.exceptions;

import org.springframework.http.HttpStatus;

public interface MALAuth0Exception extends MALException {

    HttpStatus getHttpStatus();

}
