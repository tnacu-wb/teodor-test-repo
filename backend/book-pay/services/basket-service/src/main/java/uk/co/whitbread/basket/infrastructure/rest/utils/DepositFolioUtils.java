package uk.co.whitbread.basket.infrastructure.rest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.exception.CompressionException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.in.Charge;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@UtilityClass
@Slf4j
public class DepositFolioUtils {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static byte[] getBytesFrom(List<Charge> charges) {
    try (
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream);
    ) {
      byte[] jsonBytes = mapper.writeValueAsBytes(charges);
      gzipOut.write(jsonBytes);
      gzipOut.finish();
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      var exception = new CompressionException(ErrorCode.DIGITAL_COMPRESSION_EXCEPTION,
          "Exception occurred when compressing the charges", e);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  public static List<Charge> getChargesFrom(byte[] bytes) {
    try (
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)
    ) {
      byte[] decompressed = gzipInputStream.readAllBytes();
      return mapper.readValue(decompressed,
          mapper.getTypeFactory().constructCollectionType(List.class, Charge.class));
    } catch (IOException jsonException) {
      log.warn("Failed to deserialize charges as GZIP+JSON, falling back to legacy GZIP+Java serialization: {}",
          jsonException.getMessage());
      return getChargesFromLegacy(bytes);
    }
  }

  @SuppressWarnings("unchecked")
  private static List<Charge> getChargesFromLegacy(byte[] bytes) {
    try (
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream)
    ) {
      return (List<Charge>) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      var exception = new CompressionException(ErrorCode.DIGITAL_DECOMPRESSION_EXCEPTION,
          "Exception occurred when decompressing the charges", e);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}
