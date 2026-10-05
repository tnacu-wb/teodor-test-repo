package uk.co.whitbread.cdh.utils;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpStatusCodeException;

public class CustomStatusCodeException extends HttpStatusCodeException {

  public CustomStatusCodeException() {
    super(HttpStatusCode.valueOf(500));
  }
}