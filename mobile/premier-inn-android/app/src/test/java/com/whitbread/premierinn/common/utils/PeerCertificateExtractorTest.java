package com.whitbread.premierinn.common.utils;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

/**
 * Copy YOUR_CERT_HERE file temporarily in test/resources dir and use it as an input in the following method.
 * Note: Don't commit the physical file.
 * <p>
 * This test should not run as part of the CI test suite.
 */
public class PeerCertificateExtractorTest {

    @Test
    @Ignore
    public void extractPeerCertificateFromPemTest() throws URISyntaxException {
        URL certificateUrl = PeerCertificateExtractorTest.class.getResource("/api-whitbread-one.pem");
        File certificate = new File(certificateUrl.toURI());
        String peerCertificate = PeerCertificateExtractor.extract(certificate);
        System.out.println(peerCertificate);
        assertNotNull(peerCertificate);
    }

}