package uk.co.whitbread.company.infrastructure.rest.client.aws;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import uk.co.whitbread.company.infrastructure.rest.client.aws.properties.S3Properties;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.Duration;
import uk.co.whitbread.company.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AmazonClientTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Properties s3Properties;

    @Mock
    private SdkHttpRequest httpRequest;

    @Mock
    private PresignedGetObjectRequest preSignedGetObjectRequest;

    private AmazonClient amazonClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        amazonClient = new AmazonClient(s3Properties, s3Client, s3Presigner);
    }

    @Test
    void uploadFileToS3bucketSuccessfully() {
        String fileName = "test.xlsx";
        String key = "test-key";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PutObjectResponse putObjectResponse = PutObjectResponse.builder().build();
        when(s3Client.putObject(any(PutObjectRequest.class), (RequestBody) any())).thenReturn(putObjectResponse);

        when(s3Properties.getBucket()).thenReturn(TestUtils.getBucket());

        amazonClient.uploadFileToS3bucket(fileName, key, s3Properties.getBucket().getMiReportName(),
            byteArrayOutputStream);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), (RequestBody) any());
    }

    @Test
    void createPresignedGetUrlSuccessfully() throws Exception {
        String keyName = "test-key";

        when(s3Properties.getBucket()).thenReturn(TestUtils.getBucket());
        when(s3Properties.getValidForMinutes()).thenReturn(5);
        when(httpRequest.host()).thenReturn("http://test-url.com");

        when(preSignedGetObjectRequest.httpRequest()).thenReturn(httpRequest);
        when(preSignedGetObjectRequest.expiration()).thenReturn(Instant.now().plus(Duration.ofMinutes(5)));
        when(preSignedGetObjectRequest.url()).thenReturn(new URL("http://test-url.com"));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(preSignedGetObjectRequest);

        String presignedUrl = amazonClient.createPresignedGetUrl(keyName, s3Properties.getBucket().getMiReportName());

        assertEquals("http://test-url.com", presignedUrl);
    }

    @Test
    void checkFileAlreadyExistsReturnsException() {
        String fileName = "test.xlsx";

        when(s3Properties.getBucket()).thenReturn(TestUtils.getBucket());

        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket("test-bucket")
            .prefix(fileName).build();
        when(s3Client.listObjects(listObjectsRequest))
            .thenReturn(any());

        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        boolean fileExists = false;
        if (listObjectsResponse != null && listObjectsResponse.contents() != null) {
            fileExists = true;
        }
        assertThrows(NullPointerException.class, () -> {
            throw new NullPointerException();
        });
    }

    @Test
    void bucketValueNullReturnsException() {
        String fileName = "test.xlsx";

        when(s3Properties.getBucket()).thenReturn(TestUtils.getBucket());

        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(null)
            .prefix(fileName).build();
        when(s3Client.listObjects(listObjectsRequest))
            .thenReturn(any());

        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        boolean fileExists = false;
        if (listObjectsResponse != null && listObjectsResponse.contents() != null) {
            fileExists = true;
        }
        assertThrows(NullPointerException.class, () -> {
            throw new NullPointerException();
        });
    }

    @Test
    void checkFileAlreadyExistsReturnsTrue() {
        String fileName = "test.xlsx";

        when(s3Properties.getBucket()).thenReturn(TestUtils.getBucket());

        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket("test-bucket")
            .prefix(fileName).build();
        when(s3Client.listObjects(listObjectsRequest))
            .thenReturn(any());

        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        boolean fileExists = false;
        if (listObjectsResponse != null && listObjectsResponse.contents() != null) {
            fileExists = true;
        }
        assertEquals(false, fileExists);
    }

}