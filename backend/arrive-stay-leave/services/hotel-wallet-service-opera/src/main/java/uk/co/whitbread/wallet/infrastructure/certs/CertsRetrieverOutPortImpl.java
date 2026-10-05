package uk.co.whitbread.wallet.infrastructure.certs;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Optional.ofNullable;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.wallet.ErrorCode;
import uk.co.whitbread.wallet.domain.ports.secondary.CertsRetrieverOutPort;
import uk.co.whitbread.wallet.infrastructure.certs.properties.CertProperties;
import uk.co.whitbread.wallet.infrastructure.certs.properties.S3Properties;
import uk.co.whitbread.wallet.infrastructure.exceptions.CertificateException;
import uk.co.whitbread.wallet.infrastructure.exceptions.FileNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class CertsRetrieverOutPortImpl implements CertsRetrieverOutPort {

  private final CertProperties certProperties;
  private final S3Properties s3Properties;
  private final AmazonS3 amazonS3;

  @Override
  public InputStream getP12() {
    return getCertificate(certProperties.getP12LocalPath(), certProperties.getP12S3Path(),
        certProperties.getPassword(), true);
  }

  @Override
  public InputStream getAppleWwdrca() {
    return getCertificate(certProperties.getWwdrcaLocalPath(), certProperties.getWwdrcaS3Path(),
        certProperties.getPassword(),
        false);
  }

  private byte[] getBytesFromFile(String localPath) {
    try {
      log.debug("Loading {} from disk...",  localPath);
      return IOUtils.toByteArray(new FileInputStream(localPath));
    } catch (IOException e) {
      log.warn("It is possible that the certificate is loading for the first time: {}", e.getMessage());
    }
    return new byte[]{};
  }

  public InputStream getCertificate(String localPath, String s3Path, String password,
      boolean isP12) {

    // Working with bytes, so we can create as many streams as we want throughout the validation,
    // avoiding stream exhaustion

    byte[] fileBytes = getBytesFromFile(localPath);

    if (!validateCertificate(password, isP12, fileBytes)) {
      log.info("Pulling {} from S3...", s3Path);
      fileBytes = getS3Bytes(s3Path);
      if (validateCertificate(password, isP12, fileBytes)) {
        log.info("Saving the files locally...");
        saveS3FileLocally(localPath, fileBytes);
      } else {
        log.error("There is a possibility that the S3 certificates are expired or corrupt");
        throw new CertificateException(ErrorCode.DIGITAL_CERTIFICATE_LOADING_EXCEPTION,
            "Certificate could not be loaded because it is invalid or corrupt.");
      }
    }

    return new ByteArrayInputStream(fileBytes);
  }

  private static boolean validateCertificate(String password, boolean isP12, byte[] fileBytes) {
    // Validate certificate
    try {
      if (isP12) {
        validateP12(fileBytes, password);
      } else {
        validateWwdrca(fileBytes);
      }
    } catch (Exception e) {
      ExceptionLogger.log(log,
          new CertificateException(ErrorCode.DIGITAL_CERTIFICATE_LOADING_EXCEPTION,
              "Certificate could not be loaded because it is invalid or corrupt.",
              e));
      return false;
    }
    return true;
  }

  private static void saveS3FileLocally(String localPath, byte[] certBytes) {
    try {
      log.debug("Saving certificate at {} ", localPath);
      Files.createDirectories(Path.of(localPath).getParent());
      Files.copy(new ByteArrayInputStream(certBytes), new File(localPath).toPath(),
          REPLACE_EXISTING);
    } catch (IOException e) {
      var ex = new CertificateException(ErrorCode.DIGITAL_COULD_NOT_RETRIEVE_FILE_FROM_S3,
          "Certificate could not be saved locally.", e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private static void validateP12(byte[] bytes, String password)
      throws KeyStoreException, IOException, NoSuchAlgorithmException, java.security.cert.CertificateException {
    KeyStore keystore = KeyStore.getInstance("PKCS12");
    keystore.load(new ByteArrayInputStream(bytes), password.toCharArray());

    // Get the certificate
    String alias = keystore.aliases().nextElement();
    X509Certificate x509Cert = (X509Certificate) (keystore.getCertificate(alias));

    // Get the expiration date
    Date expirationDate = x509Cert.getNotAfter();
    log.info("P12 certificate is valid until: {}", expirationDate);
  }

  private static void validateWwdrca(byte[] certBytes)
      throws java.security.cert.CertificateException {
    // Get the certificate
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    X509Certificate cert = (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(certBytes));

    log.info("WWDRCA certificate is valid until: {}", cert.getNotAfter());
  }


  private byte[] getS3Bytes(String key) {
    try {
      S3Object s3Object = amazonS3.getObject(s3Properties.getBucket().getName(), key);
      log.info("Retrieved '{}' from S3", key);
      S3ObjectInputStream s3ObjectInputStream = ofNullable(s3Object).map(S3Object::getObjectContent)
          .orElseThrow(() -> new AmazonClientException("Could not retrieve ObjectContent from S3"));

      return  IOUtils.toByteArray(s3ObjectInputStream);
    } catch (Exception e) {
      var ex = new FileNotFoundException(ErrorCode.DIGITAL_COULD_NOT_RETRIEVE_FILE_FROM_S3,
          String.format("Could not retrieve '%s' from S3 and convert into bytes", key), e);
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

}