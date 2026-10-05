package uk.co.whitbread.promo.infrastructure.adapter.s3;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.promo.infrastructure.exception.PromoBatchFileUploadException;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = {
                PromoBatchS3Uploader.class,
                PromoBatchS3UploaderItTest.WireMockS3Config.class
        }
)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class PromoBatchS3UploaderItTest {

  @Autowired
  private PromoBatchS3Uploader uploader;

  @Autowired
  private S3Client s3Client;

  @TestConfiguration
  static class WireMockS3Config {

    private static final int WIREMOCK_PORT = 8089;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
      WireMockServer server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
      server.start();

      server.stubFor(WireMock.put(WireMock.urlMatching("/.*"))
          .willReturn(WireMock.aResponse().withStatus(200)));

      server.stubFor(WireMock.get(WireMock.urlMatching("/.*"))
          .willReturn(WireMock.aResponse().withStatus(200).withBody("dummy")));

      return server;
    }

    @Bean
    @Primary
    public S3Client s3Client(WireMockServer wireMockServer) {
      return S3Client.builder()
          .endpointOverride(URI.create("http://localhost:" + wireMockServer.port()))
          .region(Region.EU_WEST_1)
          .credentialsProvider(StaticCredentialsProvider.create(
              AwsBasicCredentials.create("test", "test")))
          .serviceConfiguration(S3Configuration.builder()
              .pathStyleAccessEnabled(true)
              .build())
          .build();
    }

    @Bean
    @Primary
    public S3Presigner s3Presigner(WireMockServer wireMockServer) {
      return S3Presigner.builder()
          .endpointOverride(URI.create("http://localhost:" + wireMockServer.port()))
          .region(Region.EU_WEST_1)
          .credentialsProvider(StaticCredentialsProvider.create(
              AwsBasicCredentials.create("test", "test")))
          .build();
    }
  }

  @Test
  void uploadExcel_shouldWorkWithWireMock() {
    UUID batchId = UUID.randomUUID();
    byte[] excelBytes = "excel-data".getBytes();

    PromoBatchEntity entity = PromoBatchEntity.builder()
        .operaPromoCode("FX10R")
        .campaignName("HEAPTI")
        .build();

    String key = uploader.uploadExcel(batchId, excelBytes, entity);

    System.out.println("S3 Key: " + key);

    assertTrue(key.contains("FX10R"));
  }

  @Test
  void uploadCsv_shouldUploadAndReturnKey() {
    UUID batchId = UUID.randomUUID();
    byte[] csvBytes = "col1,col2\nval1,val2".getBytes();

    String key = uploader.uploadCsv(batchId, csvBytes);

    assertEquals("promo-batches/" + batchId + "/promo-codes.csv", key);
  }

  @Test
  void uploadCsv_shouldWorkEvenWhenBytesEmpty() {
    UUID batchId = UUID.randomUUID();
    byte[] csvBytes = new byte[0]; // empty

    String key = uploader.uploadCsv(batchId, csvBytes);

    assertNotNull(key);
    assertTrue(key.contains("promo-batches"));
  }

  @Test
  void uploadExcel_shouldThrow_whenBytesNull() {
    UUID batchId = UUID.randomUUID();
    PromoBatchEntity entity = PromoBatchEntity.builder()
        .operaPromoCode("FX10R").campaignName("HEAPTI").build();

    assertThrows(
        PromoBatchFileUploadException.class,
        () -> uploader.uploadExcel(batchId, null, entity)
    );
  }

  @Test
  void zipExcel_shouldCreateZipWithCorrectFileName() throws Exception {
    byte[] excelBytes = "data".getBytes();
    String fileName = "test.xlsx";

    byte[] zipBytes = uploader.zipExcel(excelBytes, fileName);

    try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(
        new java.io.ByteArrayInputStream(zipBytes))) {
      java.util.zip.ZipEntry entry = zis.getNextEntry();
      assertNotNull(entry);
      assertEquals(fileName, entry.getName());

      byte[] buffer = new byte[excelBytes.length];
      int read = zis.read(buffer);
      assertEquals(excelBytes.length, read);
      assertArrayEquals(excelBytes, buffer);
    }
  }
}