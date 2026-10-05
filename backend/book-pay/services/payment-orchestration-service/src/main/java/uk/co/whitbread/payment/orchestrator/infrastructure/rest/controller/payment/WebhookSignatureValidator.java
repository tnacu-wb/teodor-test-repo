package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransWebhookProperties;

/**
 * Verifies the authenticity of Datatrans webhook callbacks.
 *
 * <p>Datatrans signs each webhook with an HMAC and sends it in the
 * {@code Datatrans-Signature} header, formatted as
 * {@code t=<timestampMillis>,s0=<hex-hmac-sha256>}. The signed content is the timestamp
 * value concatenated with the <em>exact</em> raw request body, keyed with the merchant's
 * hex-encoded sign key. Callers must therefore validate against the raw body bytes, since
 * re-serialising a parsed model would not reproduce the signed content.
 *
 * <p>Comparison is constant-time and no signature material or key is ever logged.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookSignatureValidator {

  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private static final String TIMESTAMP_PART = "t";
  private static final String SIGNATURE_PART = "s0";

  private final DatatransWebhookProperties properties;

  /**
   * Validates the {@code Datatrans-Signature} header against the raw request body.
   *
   * @param rawBody the exact request body as received, decoded as UTF-8
   * @param signatureHeader the {@code Datatrans-Signature} header value, may be {@code null}
   * @return {@code true} when the signature is valid, or when validation is explicitly
   *     disabled by configuration; {@code false} otherwise
   */
  public boolean isValid(String rawBody, String signatureHeader) {
    if (!properties.isValidationEnabled()) {
      // Documented escape hatch for local/integration profiles only. Deployed environments
      // must keep integrations.datatrans.webhook.validation-enabled = true (the default).
      log.warn("Datatrans webhook signature validation is disabled — accepting webhook unverified");
      return true;
    }

    String hmacKey = properties.getHmacKey();
    if (hmacKey == null || hmacKey.isBlank()) {
      log.warn("Rejected Datatrans webhook: no HMAC sign key configured");
      return false;
    }
    if (signatureHeader == null || signatureHeader.isBlank()) {
      log.warn("Rejected Datatrans webhook: Datatrans-Signature header missing");
      return false;
    }
    if (rawBody == null) {
      log.warn("Rejected Datatrans webhook: request body missing");
      return false;
    }

    Map<String, String> parts = parse(signatureHeader);
    String timestamp = parts.get(TIMESTAMP_PART);
    String provided = parts.get(SIGNATURE_PART);
    if (timestamp == null || timestamp.isBlank() || provided == null || provided.isBlank()) {
      log.warn("Rejected Datatrans webhook: Datatrans-Signature header malformed");
      return false;
    }

    byte[] key = decodeKey(hmacKey);
    if (key == null) {
      return false;
    }

    String computed = hmacSha256Hex(key, timestamp + rawBody);
    if (computed == null) {
      return false;
    }

    boolean valid = MessageDigest.isEqual(
        computed.getBytes(StandardCharsets.UTF_8),
        provided.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
    if (!valid) {
      log.warn("Rejected Datatrans webhook: signature mismatch");
    }
    return valid;
  }

  /**
   * Splits the signature header into its {@code key=value} components.
   *
   * @param signatureHeader the raw header value
   * @return the parsed components, keyed by component name
   */
  private Map<String, String> parse(String signatureHeader) {
    Map<String, String> parts = new HashMap<>();
    for (String element : signatureHeader.split(",")) {
      String trimmed = element.trim();
      int separator = trimmed.indexOf('=');
      if (separator > 0) {
        parts.put(trimmed.substring(0, separator).trim(), trimmed.substring(separator + 1).trim());
      }
    }
    return parts;
  }

  /**
   * Hex-decodes the configured sign key.
   *
   * @param hmacKey the hex-encoded key
   * @return the key bytes, or {@code null} when the key is not valid hex
   */
  private byte[] decodeKey(String hmacKey) {
    try {
      return HexFormat.of().parseHex(hmacKey);
    } catch (IllegalArgumentException e) {
      log.warn("Rejected Datatrans webhook: configured HMAC sign key is not valid hex");
      return null;
    }
  }

  /**
   * Computes the lower-case hex HMAC-SHA256 of the signed content.
   *
   * @param key the hex-decoded sign key
   * @param signedContent the timestamp concatenated with the raw body
   * @return the hex digest, or {@code null} when the HMAC could not be computed
   */
  private String hmacSha256Hex(byte[] key, String signedContent) {
    try {
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
      return HexFormat.of().formatHex(mac.doFinal(signedContent.getBytes(StandardCharsets.UTF_8)));
    } catch (GeneralSecurityException e) {
      log.warn("Rejected Datatrans webhook: unable to compute HMAC [{}]", e.getMessage());
      return null;
    }
  }
}
