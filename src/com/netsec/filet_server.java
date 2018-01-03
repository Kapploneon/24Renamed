package com.netsec;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

public class filet_server {
    public filet_server() {
    }

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = null;
        int count;
        Boolean sesEst = false;
        Boolean whileLoop = false;
        String sessionKeyString = "";
        byte[] sessionKeyBytes = new byte[16*1024];
        byte[] bytes = new byte[16*1024];

        // Creating Server Socket.
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;

        // Waiting for the client Connection...
        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        // Input Stream
        try {
            in = socket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        while(!whileLoop)
        {
            in.read(bytes);
            String x = new String(bytes);
            switch (x) {
                case "Who are you?":
                    sendCert(socket);
                    whileLoop = true;
                    break;
                default:
                    whileLoop = true;
                    break;
            }
        }

        whileLoop = false;

        // Some message after proving identity and before sending key.
        // using public key encryption.
        while(!whileLoop)
        {
            String x = new String(bytes);
            x = x.substring(0,7);
            switch (x) {
                case "NextKey":
                    whileLoop = true;
                    break;
                default:
                    whileLoop = true;
                    break;
            }
        }

        while(!sesEst) {

            byte[] keyByte = new byte[256];

            in.read(keyByte,0,keyByte.length);

            String decryptedPlain = decryptCipher(keyByte);

            if (decryptedPlain.regionMatches(0, "Key:", 0, 4)) {
                sessionKeyString = decryptedPlain;
                sesEst = true;
            }
            System.out.println(decryptedPlain);
        }

        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        outputStream.flush();

        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        Boolean eXit = false;
        String options = "What do you want to do:\n1.Upload File.\n2.Download File.\n3.Close connection.";
        outputStream.writeObject(options);
        int  response;

        response = (int) inputStream.readObject();

        if(response == 1){

            // Output Stream
            try {
                out = new FileOutputStream((String) inputStream.readObject());
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }

            SHA1_new sha1 = new SHA1_new();

            sessionKeyBytes = sessionKeyString.getBytes();
            sessionKeyString = sha1.filet_sha1(sessionKeyBytes);
            StringXORer xoRer = new StringXORer();
            String xorResult;


            while ((count = in.read(bytes)) > 0) {

//                System.out.println(new String (bytes));

                xorResult = xoRer.decode(new String (bytes),sessionKeyString);
                bytes = xorResult.getBytes();

//                System.out.println(xorResult);
//                System.out.println(new String (bytes));

                out.write(bytes, 0, bytes.length);

                sessionKeyBytes = sessionKeyString.getBytes();
                sessionKeyString = sha1.filet_sha1(sessionKeyBytes);
            }
        }
        else if (response == 2){
            // The client wish to download file.

            String filename = (String) inputStream.readObject();
            File name = new File(filename);
            if ( name.exists() && name.isFile() )
            {
                File file = new File(name.getName());
                SHA1_new sha1 = new SHA1_new();

                // Input Stream.
                InputStream inp = new FileInputStream(file);

                sessionKeyBytes = sessionKeyString.getBytes();
                sessionKeyString = sha1.filet_sha1(sessionKeyBytes);
                StringXORer xoRer = new StringXORer();
                String xorResult;

                while ((count = inp.read(bytes)) > 0 ){

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


            }

        }
        else if (response == 3){
            // Output Stream
            try {
                out = new FileOutputStream((String) inputStream.readObject());
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }
            int x = 0;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
                System.out.println((String.valueOf(x)));
                x++;
            }
        }

        out.close();
        in.close();
        socket.close();
        serverSocket.close();
    }

    private static String decryptCipher(byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        File privKeyFile = new File("private_key_s1.der");
        // read private key DER file
        DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
        byte[] privKeyBytes = new byte[(int) privKeyFile.length()];
        dis.read(privKeyBytes);
        dis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        //keyFactory.initialize(2048,random);

        // decode private key
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
        PrivateKey privKey = (PrivateKey) keyFactory.generatePrivate(privSpec);

        // decrypt the ciphertext using the private key
        System.out.println("\nStart decryption");
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] newPlainText = new byte[16*1024];
        newPlainText = cipher.doFinal(bytes);
        System.out.println("Finish decryption: ");
        String decryptedPlain = new String(newPlainText, "UTF8");

        return decryptedPlain;
    }

    private static void sendCert(Socket socket) throws IOException {
        BufferedOutputStream outToClient = null;

        outToClient = new BufferedOutputStream(socket.getOutputStream());

        if (outToClient != null) {
            File myFile = new File("server-certificate.crt");
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = null;

            try {
                fis = new FileInputStream(myFile);
            } catch (FileNotFoundException ex) {
                // Do exception handling
            }
            BufferedInputStream bis = new BufferedInputStream(fis);

            try {
                bis.read(mybytearray, 0, mybytearray.length);
                outToClient.write(mybytearray, 0, mybytearray.length);
                outToClient.flush();
                outToClient.close();
                // File sent, exit the main method
            } catch (IOException ex) {
                // Do exception handling
            }
        }
    }
}