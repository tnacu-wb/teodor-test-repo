package uk.co.whitbread.ondemandrefreshservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.mapper.HotelAvailabilitiesMapperTest;

public final class MockDataReader {
  private static final ObjectMapper mapper = new ObjectMapper();
  public static String readFromInputStream(final InputStream inputStream)
      throws IOException {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (final BufferedReader br
        = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    }
    return resultStringBuilder.toString();
  }

  public static <T> T generateMockRsp(final String filePath, Class<T> valueType) throws IOException {
    final InputStream inventoryInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream(filePath);
    return mapper.readValue(readFromInputStream(inventoryInputStream), valueType);
  }

}
