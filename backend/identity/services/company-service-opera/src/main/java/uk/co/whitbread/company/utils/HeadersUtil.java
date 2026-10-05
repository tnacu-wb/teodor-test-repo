package uk.co.whitbread.company.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HeadersUtil {

  private HeadersUtil() {

  }

  public static <T> ResponseEntity<T> makeNotCacheable(T response) {
    return new ResponseEntity<>(response, getNoCacheHeaders(), HttpStatus.OK);
  }

  public static <T> ResponseEntity<T> makeNotCacheableNoContent() {
    return new ResponseEntity<>(getNoCacheHeaders(), HttpStatus.NO_CONTENT);
  }

  private static HttpHeaders getNoCacheHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Pragma", "no-cache");
    headers.add("Cache-Control", "no-cache");
    headers.add("Expires", "0");
    return headers;
  }
}
