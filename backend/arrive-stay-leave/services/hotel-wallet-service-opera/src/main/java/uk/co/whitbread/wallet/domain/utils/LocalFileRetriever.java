package uk.co.whitbread.wallet.domain.utils;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.wallet.ErrorCode;
import uk.co.whitbread.wallet.domain.exception.PassJsonCreationException;

@Slf4j
public class LocalFileRetriever {

  private LocalFileRetriever() {
  }

  public static InputStream getFileFromClasspath(String path) {
    try {
      return new ClassPathResource(path).getInputStream();
    } catch (IOException e) {
      var ex = new PassJsonCreationException(ErrorCode.DIGITAL_COULD_NOT_FIND_FILE_EXCEPTION,
          "Could not find file on path=" + path, e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  public static String getStringFromInputStream(InputStream inputStream) {
    try {
      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      var ex = new PassJsonCreationException(
          ErrorCode.DIGITAL_COULD_NOT_READ_PASS_INPUT_STREAM_EXCEPTION,
          "Couldn't read pass input stream", e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  public static Map<String, InputStream> getFilesInClasspathDirectory(Map<String, InputStream> files,
      String directory) {

    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    try {
      for (Resource resource : resolver.getResources("classpath:" + directory + "/**")) {
        String resourceFullPath = resource.getURL().toString();
        if (!resourceFullPath.endsWith("/")) {
          String resourcePath = resourceFullPath.substring(
              resourceFullPath.indexOf(directory) + directory.length() + 1);
          files.put(resourcePath, resource.getInputStream());
        }
      }
    } catch (IOException e) {
      var ex = new PassJsonCreationException(
          ErrorCode.DIGITAL_COULD_NOT_RESOLVE_DIRECTORY_EXCEPTION,
          "Cannot resolve pass templates directory at path=" + directory, e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    return files;
  }

}
