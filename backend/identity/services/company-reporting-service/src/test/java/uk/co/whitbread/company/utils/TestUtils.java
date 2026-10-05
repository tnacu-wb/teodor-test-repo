package uk.co.whitbread.company.utils;

import uk.co.whitbread.company.infrastructure.rest.client.aws.properties.S3Properties.Bucket;

public class TestUtils {

  public static Bucket getBucket() {
    Bucket bucket = new Bucket();
    bucket.setMiReportName("test-bucket");
    bucket.setEmergencyReportName("test-bucket");
    bucket.setRegion("test-region");
    bucket.setAccessKey("test-access");
    bucket.setSecretAccessKey("test-secret");
    return bucket;
  }

}
