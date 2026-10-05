package uk.co.whitbread.wallet.infrastructure.certs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.wallet.infrastructure.certs.properties.CertProperties;
import uk.co.whitbread.wallet.infrastructure.certs.properties.S3Properties;
import uk.co.whitbread.wallet.infrastructure.certs.properties.S3Properties.Bucket;
import uk.co.whitbread.wallet.infrastructure.exceptions.FileNotFoundException;

@ExtendWith(MockitoExtension.class)
class CertsRetrieverOutPortImplTest {

  @InjectMocks
  private CertsRetrieverOutPortImpl certsRetrieverOutPort;

  @Mock
  private CertProperties certProperties;
  @Mock
  private S3Properties s3Properties;
  @Mock
  private AmazonS3 amazonS3;

  @BeforeEach
  void setup() {
    when(certProperties.getPassword()).thenReturn("pippobaudo");
  }

  @Test
  void getP12fromS3Success() {
    String localPath = "test/certs/PassTypeCertificate.p12";
    when(certProperties.getP12LocalPath()).thenReturn(localPath);
    when(certProperties.getP12S3Path()).thenReturn("certs/PassTypeCertificate.p12");

    if (!Path.of(localPath).toFile().exists()) {
      Bucket bucket = new Bucket();
      bucket.setName("my-bucket");
      when(s3Properties.getBucket()).thenReturn(bucket);

      InputStream p12Cert = this.getClass().getClassLoader()
          .getResourceAsStream(certProperties.getP12S3Path());
      S3Object s3Object = new S3Object();
      s3Object.setObjectContent(new S3ObjectInputStream(p12Cert, new HttpRequestBase() {
        @Override
        public String getMethod() {
          return "";
        }
      }));
      when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3Object);
    }

    assertThat(certsRetrieverOutPort.getP12()).isNotNull();

  }

  @Test
  void getP12LocallySuccess() throws IOException {
    when(certProperties.getP12LocalPath()).thenReturn("test/certs/PassTypeCertificate.p12");
    when(certProperties.getP12S3Path()).thenReturn("certs/PassTypeCertificate.p12");

    InputStream p12Cert = this.getClass().getClassLoader()
        .getResourceAsStream("certs/PassTypeCertificate.p12");
    assert p12Cert != null;
    byte[] certBytes = IOUtils.toByteArray(p12Cert);

    try (MockedStatic<IOUtils> mockedIOUtils = mockStatic(IOUtils.class);
        MockedConstruction<FileInputStream> ignored = mockConstruction(
            FileInputStream.class)) {
      mockedIOUtils.when(() -> IOUtils.toByteArray(any(FileInputStream.class)))
          .thenReturn(certBytes);

      assertThat(certsRetrieverOutPort.getP12()).isNotNull();
    }
  }

  @Test
  void getWwdrcaSuccess() {
    String localPath = "test/certs/AppleWWDRCA.cer";
    when(certProperties.getWwdrcaLocalPath()).thenReturn(localPath);
    when(certProperties.getWwdrcaS3Path()).thenReturn("certs/AppleWWDRCA.cer");
    if (!Path.of(localPath).toFile().exists()) {
      Bucket bucket = new Bucket();
      bucket.setName("my-bucket");
      when(s3Properties.getBucket()).thenReturn(bucket);

      InputStream wwdrcaCert = this.getClass().getClassLoader()
          .getResourceAsStream(certProperties.getWwdrcaS3Path());
      S3Object s3Object = new S3Object();
      s3Object.setObjectContent(new S3ObjectInputStream(wwdrcaCert, new HttpRequestBase() {
        @Override
        public String getMethod() {
          return "";
        }
      }));

      when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3Object);
    }

    assertThat(certsRetrieverOutPort.getAppleWwdrca()).isNotNull();
  }

  @Test
  void getP12_ThrowsFileNotFoundException_WhenFetchingFromS3() {
    Bucket bucket = new Bucket();
    bucket.setName("my-bucket");
    when(s3Properties.getBucket()).thenReturn(bucket);

    when(certProperties.getP12LocalPath()).thenReturn("other-test/certs/PassTypeCertificate.p12");
    when(certProperties.getP12S3Path()).thenReturn("certs/PassTypeCertificate.p12");
    when(amazonS3.getObject(anyString(), anyString())).thenThrow(
        new AmazonServiceException("Bucket not found!"));

    assertThrows(FileNotFoundException.class, () -> certsRetrieverOutPort.getP12());
  }

}
