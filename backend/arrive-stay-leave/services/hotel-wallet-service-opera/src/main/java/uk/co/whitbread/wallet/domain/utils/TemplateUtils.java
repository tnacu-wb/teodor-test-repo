package uk.co.whitbread.wallet.domain.utils;

import de.brendamour.jpasskit.signing.IPKPassTemplate;
import de.brendamour.jpasskit.signing.PKPassTemplateInMemory;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.wallet.ErrorCode;
import uk.co.whitbread.wallet.domain.exception.WalletCreationException;

@Slf4j
public class TemplateUtils {

  private static final DateTimeFormatter CHECK_IN_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "dd/MM/yyyy, ha");

  private TemplateUtils() {
  }

  public static IPKPassTemplate getPkPassTemplate(String templateDir, String type) {
    try {
      PKPassTemplateInMemory pkPassTemplateInMemory = new PKPassTemplateInMemory();
      Map<String, InputStream> templateFiles = LocalFileRetriever.getFilesInClasspathDirectory(
          new HashMap<>(), templateDir);
      for (Entry<String, InputStream> file : templateFiles.entrySet()) {
        pkPassTemplateInMemory.addFile(file.getKey(), file.getValue());
      }
      String imagesDir = String.format("%s%s", templateDir, type);
      Map<String, InputStream> imagesFiles = LocalFileRetriever.getFilesInClasspathDirectory(
          new HashMap<>(), imagesDir);
      for (Entry<String, InputStream> file : imagesFiles.entrySet()) {
        pkPassTemplateInMemory.addFile(file.getKey(), file.getValue());
      }

      return pkPassTemplateInMemory;
    } catch (Exception e) {
      var ex = new WalletCreationException(ErrorCode.DIGITAL_BAD_TEMPLATE_CONFIGURATION_EXCEPTION,
          String.format("Bad configuration of template in templates directory=%s", templateDir), e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }

  }

  public static String getTime(LocalDate date, String time) {
    return date.atTime(LocalTime.parse(time)).format(CHECK_IN_DATE_TIME_FORMATTER);
  }
}
