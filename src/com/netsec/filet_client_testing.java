package com.netsec;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class filet_client_testing {

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) throws Exception {
        Socket socket = null;
        String host = "192.168.0.8";
        byte[] bytes = new byte[16 * 1024];
        int count;

        // Socket declaration.
        socket = new Socket(host, 4444);

        // Output Stream.
        OutputStream out = socket.getOutputStream();
        InputStream input = socket.getInputStream();
        Boolean estSession = false;

        while (!estSession) {
            File ServerCertFile = new File("server-certificate.crt");
            if (ServerCertFile.exists()) {
                String ServerCert = readFile("server-certificate.crt", Charset.defaultCharset());
                ServerCert = ServerCert.replace("-----BEGIN CERTIFICATE-----\n", "");
                ServerCert = ServerCert.replace("-----END CERTIFICATE-----\n", "");

                byte[] encodedCert = ServerCert.getBytes("UTF-8");
                byte[] decodedCert = Base64.decodeBase64(encodedCert);
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                InputStream in = new ByteArrayInputStream(decodedCert);
                X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(in);
                PublicKey publicKey = certificate.getPublicKey();

                // get an RSA cipher object and print the provider
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

                // create a Scanner to obtain session key from the command window
                Scanner inputKey = new Scanner(System.in);
                System.out.print("Enter session key: "); // prompt
                String sessionKey = "Key:" + inputKey.nextLine() + "appendingExtra";
                byte[] plainText = sessionKey.getBytes("UTF8");

                System.out.println("\nStart encryption");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] cipherText = new byte[256];
                cipherText = cipher.doFinal(plainText);

//                bytes = cipher.doFinal(plainText);
                System.out.println("Finish encryption: ");

                // Sending the Session key to the server.
                try {
                    String preText = "NextKey";
                    byte[] preBytes = new byte[16*1024];
                    preBytes = preText.getBytes();
                    out.write(preBytes, 0, preBytes.length);
                    out.write(cipherText, 0, cipherText.length);
                }
                catch (IOException ex) {
                    System.out.println("Can't get socket input stream. ");
                }
//                out.write(bytes, 0, bytes.length);
                out.flush();

                estSession= true;

            } else {
                // Get the file from the server.
                String OriginalPlainText = "Who are you?";
                bytes = OriginalPlainText.getBytes("UTF8");

                out.write(bytes,0,bytes.length);
                out.flush();

                while ((count = input.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
            }
        }

        File file = new File("testingtransferubunt");
        // Get the size of the file
        long length = file.length();

        // Input Stream.
        InputStream in = new FileInputStream(file);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.flush();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        String getSerReply = (String) objectInputStream.readObject();
        System.out.println(getSerReply);

        System.out.println("Enter your response below: ");
        Scanner inputKey = new Scanner(System.in);
        int sessionKey = inputKey.nextInt();
        Boolean repPro = false;

        while(!repPro){

            if(sessionKey < 1 || sessionKey > 3){
                System.out.println("Invalid response. Please select one of the following");
                System.out.println(getSerReply);
                System.out.println("Enter your correct response below: ");
                sessionKey = inputKey.nextInt();
            }
            else{
                repPro = true;
            }
        }

        objectOutputStream.writeObject(sessionKey);

        if (sessionKey == 1){

            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

        }


        // Closing the streams and socket.
        out.close();
        in.close();
        socket.close();

    }
}