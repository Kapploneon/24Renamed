package com.netsec;

import com.java24hours.FileDemonstration;
import oracle.jrockit.jfr.JFR;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.swing.*;
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
        String sessionKey;
        byte[] sessionKeyBytes = new byte[16 * 1024];
        byte[] cipherText = new byte[256];
        Scanner inputKey = new Scanner(System.in);
        int optionSelected;

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
                System.out.print("Enter session key: "); // prompt
                sessionKey = "Key:" + inputKey.nextLine() + "appendingExtra";
                sessionKeyBytes = sessionKey.getBytes("UTF8");

                System.out.println("\nStart encryption");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                cipherText = cipher.doFinal(sessionKeyBytes);
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
                out.flush();

                estSession= true;

            } else {
                // Get the file from the server.
                String OriginalsessionKeyBytes = "Who are you?";
                bytes = OriginalsessionKeyBytes.getBytes("UTF8");

                out.write(bytes,0,bytes.length);
                out.flush();

                while ((count = input.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
            }
        }

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.flush();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        String getSerReply = (String) objectInputStream.readObject();
        System.out.println(getSerReply);

        System.out.println("Enter your response below: ");

        optionSelected = inputKey.nextInt();

        Boolean repPro = false;

        while(!repPro){

            if(optionSelected < 1 || optionSelected > 3){
                System.out.println("Invalid response. Please select one of the following");
                System.out.println(getSerReply);
                System.out.println("Enter your correct response below: ");
                optionSelected = inputKey.nextInt();
            }
            else{
                repPro = true;
            }
        }

        objectOutputStream.writeObject(optionSelected);

        if (optionSelected == 1){

            FileDemonstration application = new FileDemonstration();
            application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            File name = application.getFileOrDirectory();

            if ( name.exists() && name.isFile() ) // if name exists, upload that file to the server.
            {
                objectOutputStream.writeObject(name.getName());

                File file = new File(name.getPath());
                
                SHA1_new sha1 = new SHA1_new();
                
                // Input Stream.
                InputStream in = new FileInputStream(file);

                String sessionKeyString = sha1.filet_sha1(sessionKeyBytes);
                StringXORer xoRer = new StringXORer();
                String xorResult;

                while ((count = in.read(bytes)) > 0) {

                    System.out.println(new String (bytes));

                    xorResult = xoRer.encode(new String(bytes),sessionKeyString);
                    bytes = xorResult.getBytes();

                    System.out.println(xorResult);
                    System.out.println(new String (bytes));

                    out.write(bytes, 0, bytes.length);

                    // Compute new sessionKeyBytes.
                    sessionKeyBytes = sessionKeyString.getBytes();
                    sessionKeyString = sha1.filet_sha1(sessionKeyBytes);

                }
                in.close();
            }
        }
        else if (optionSelected == 2){

            System.out.println("Enter the file name below:");
            String filename = inputKey.nextLine();

            objectOutputStream.writeObject(filename);

        }
        else if (optionSelected == 3){

            FileDemonstration application = new FileDemonstration();
            application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            File name = application.getFileOrDirectory();

            if ( name.exists() && name.isFile() ) // if name exists, upload that file to the server.
            {
                objectOutputStream.writeObject(name.getName());

                File file = new File(name.getPath());

                // Input Stream.
                InputStream in = new FileInputStream(file);

                int x = 0;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                    System.out.println(String.valueOf(x));
                    x++;
                }

                in.close();
            }

        }

        // Closing the streams and socket.
        out.close();
//        in.close();
        socket.close();

    }
}