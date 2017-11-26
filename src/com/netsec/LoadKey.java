package com.netsec;

import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;

class LoadKey {
    public static void main(String[] args) throws Exception
    {
        String cert1 = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDITCCAoqgAwIBAgIQT52W2WawmStUwpV8tBV9TTANBgkqhkiG9w0BAQUFADBM\n" +
                "MQswCQYDVQQGEwJaQTElMCMGA1UEChMcVGhhd3RlIENvbnN1bHRpbmcgKFB0eSkg\n" +
                "THRkLjEWMBQGA1UEAxMNVGhhd3RlIFNHQyBDQTAeFw0xMTEwMjYwMDAwMDBaFw0x\n" +
                "MzA5MzAyMzU5NTlaMGgxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlh\n" +
                "MRYwFAYDVQQHFA1Nb3VudGFpbiBWaWV3MRMwEQYDVQQKFApHb29nbGUgSW5jMRcw\n" +
                "FQYDVQQDFA53d3cuZ29vZ2xlLmNvbTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkC\n" +
                "gYEA3rcmQ6aZhc04pxUJuc8PycNVjIjujI0oJyRLKl6g2Bb6YRhLz21ggNM1QDJy\n" +
                "wI8S2OVOj7my9tkVXlqGMaO6hqpryNlxjMzNJxMenUJdOPanrO/6YvMYgdQkRn8B\n" +
                "d3zGKokUmbuYOR2oGfs5AER9G5RqeC1prcB6LPrQ2iASmNMCAwEAAaOB5zCB5DAM\n" +
                "BgNVHRMBAf8EAjAAMDYGA1UdHwQvMC0wK6ApoCeGJWh0dHA6Ly9jcmwudGhhd3Rl\n" +
                "LmNvbS9UaGF3dGVTR0NDQS5jcmwwKAYDVR0lBCEwHwYIKwYBBQUHAwEGCCsGAQUF\n" +
                "BwMCBglghkgBhvhCBAEwcgYIKwYBBQUHAQEEZjBkMCIGCCsGAQUFBzABhhZodHRw\n" +
                "Oi8vb2NzcC50aGF3dGUuY29tMD4GCCsGAQUFBzAChjJodHRwOi8vd3d3LnRoYXd0\n" +
                "ZS5jb20vcmVwb3NpdG9yeS9UaGF3dGVfU0dDX0NBLmNydDANBgkqhkiG9w0BAQUF\n" +
                "AAOBgQAhrNWuyjSJWsKrUtKyNGadeqvu5nzVfsJcKLt0AMkQH0IT/GmKHiSgAgDp\n" +
                "ulvKGQSy068Bsn5fFNum21K5mvMSf3yinDtvmX3qUA12IxL/92ZzKbeVCq3Yi7Le\n" +
                "IOkKcGQRCMha8X2e7GmlpdWC1ycenlbN0nbVeSv3JUMcafC4+Q==\n" +
                "-----END CERTIFICATE-----\n";

        String cert = "-----BEGIN CERTIFICATE-----\n" +
                "MIIDvTCCAqUCCQDOopyqHmBlkjANBgkqhkiG9w0BAQUFADCBoDELMAkGA1UEBhMC\n" +
                "VVMxDjAMBgNVBAgMBVRleGFzMQ8wDQYDVQQHDAZEYWxsYXMxETAPBgNVBAoMCFVU\n" +
                "RGFsbGFzMSQwIgYDVQQLDBtDb21wdXRlciBTY2llbmNlIERlcGFydG1lbnQxEzAR\n" +
                "BgNVBAMMCmFzaGthbi1VVEQxIjAgBgkqhkiG9w0BCQEWE2FzaGthbkB1dGRhbGxh\n" +
                "cy5lZHUwHhcNMTcxMTE0MTkyOTIwWhcNMTcxMjE0MTkyOTIwWjCBnzELMAkGA1UE\n" +
                "BhMCVVMxDjAMBgNVBAgMBVRleGFzMRMwEQYDVQQHDApSaWNoYXJkc29uMREwDwYD\n" +
                "VQQKDAhVVERhbGxhczEMMAoGA1UECwwDVVREMRgwFgYDVQQDDA9VVERhbGxhcy1D\n" +
                "UzYzNDkxMDAuBgkqhkiG9w0BCQEWIWluZm9AVVREYWxsYXMtQ1M2MzQ5LnV0ZGFs\n" +
                "bGFzLmVkdTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMxkeei7xjUR\n" +
                "+b4bY8fRHUVlTXGELW73ZOlZ8gIQoQgdcqrgx7qE2hdlsQn9gdA0C3avjpjsFB6+\n" +
                "BB7/c4PgvzSXdY8MjTmX+JxPKkw4CMw1Yy/aNtCZ2if5BAEYziCQABKXDElK8GwZ\n" +
                "wcJrHV4hohSksOwPJBQwpuTiKntmz3uRlTEY45+P5f1Vp+QGU0SxdkjcxErJbClp\n" +
                "519AK7fUTByLbf1rF11AJkFmW6Ge984REk/iuiw9Uo0ZSDq4lD/6siCJzUuFQ1xb\n" +
                "Tw7qePijmdx7JIfmwipaBlOD1icwkrohDiGp9rtpTAfotqRsSBxUbrLXGaygOIRW\n" +
                "zWKYnxRgUJkCAwEAATANBgkqhkiG9w0BAQUFAAOCAQEAV3NaR+kv93MhpiNd0CSX\n" +
                "A8FQbNR1FyBsdJy6FdtvOWGnHjVd5y1EYmmtTv5iagCGYcO0jaf8nj1EYlWIpxqd\n" +
                "KMLmiLrOlKWHZoQQanUoa+aBnenYw0eqmSnfY70pyuqyGyyrLuQ1jqE63VVUZgcB\n" +
                "7Xo+AdRV1RM0EcAF801czWLPY08wRnT8M7XNwGhvje7MnKWsRKtRhS6qPEfAOhsf\n" +
                "r5BdR53jUSjX7+SMicNjEAX8NvrtP+9V/DsuE2U8zG4akrMkLECQDnIdDFzzJmgS\n" +
                "6piuWoVAN6rg2GSmZIEEoqUvbfEO4dbEx0sUugRYWzfM8OeZS92h/3kWJwHJQzgQ\n" +
                "gw==\n" +
                "-----END CERTIFICATE-----\n";

        cert = cert.replace("-----BEGIN CERTIFICATE-----\n", "");
        cert = cert.replace("-----END CERTIFICATE-----\n", "");
        System.out.println(cert);

        String OriginalPlainText = "My name is Karan.";
        byte[] plainText = OriginalPlainText.getBytes("UTF8");


        byte[] encodedCert = cert.getBytes("UTF-8");
        byte[] decodedCert = Base64.decodeBase64(encodedCert);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        InputStream in = new ByteArrayInputStream(decodedCert);
        X509Certificate certificate = (X509Certificate)certFactory.generateCertificate(in);

        System.out.println("Subject DN : " + certificate.getSubjectDN().getName());
        System.out.println("Issuer : " + certificate.getIssuerDN().getName());
        System.out.println("Not After: " + certificate.getNotAfter());
        System.out.println("Not Before: " + certificate.getNotBefore());
        System.out.println("version: " + certificate.getVersion());
        System.out.println("serial number : " + certificate.getSerialNumber());

        PublicKey publicKey = certificate.getPublicKey();
        System.out.println("PublicKey : \n" + publicKey);

        //
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        System.out.println( "\n" + cipher.getProvider().getInfo() );
        //
        // encrypt the plaintext using the public key
        System.out.println( "\nStart encryption" );
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = cipher.doFinal(plainText);
        System.out.println( "Finish encryption: " );
        System.out.println( new String(cipherText, "UTF8") );
        //

        File privKeyFile = new File("private_key_s1.der");
        // read private key DER file
        DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
        byte[] privKeyBytes = new byte[(int)privKeyFile.length()];
        dis.read(privKeyBytes);
        dis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        //keyFactory.initialize(2048,random);

        // decode private key
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
        PrivateKey privKey = (PrivateKey) keyFactory.generatePrivate(privSpec);

        // decrypt the ciphertext using the private key
        System.out.println( "\nStart decryption" );
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] newPlainText = cipher.doFinal(cipherText);
        System.out.println( "Finish decryption: " );
        System.out.println( new String(newPlainText, "UTF8") );

    }
}