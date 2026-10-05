package com.whitbread.premierinn.common.utils;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * This class is used for testing purposes only.
 * Use this class to extract pins from Physical cert files.
 */
public class PeerCertificateExtractor {

    /**
     * Get peer certificate(Public key to sha256 to base64)
     *
     * @param certificate Crt or der or pem file with a valid certificate
     * @return
     */
    @SuppressLint("NewApi")
    public static String extract(File certificate) {

        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(certificate);
            X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X509")
                    .generateCertificate(inputStream);

            byte[] publicKeyEncoded = x509Certificate.getPublicKey().getEncoded();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] publicKeySha256 = messageDigest.digest(publicKeyEncoded);
            byte[] publicKeyShaBase64 = Base64.getEncoder().encode(publicKeySha256);

            return "sha256/" + new String(publicKeyShaBase64);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }
}
