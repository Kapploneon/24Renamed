package com.netsec;

import java.io.*;
import java.net.Socket;

public class filet_client {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String host = "192.168.0.8";

        // Socket declaration.
        socket = new Socket(host, 4444);

        File ServerCertFile = new File("server-certificate.crt");
        String ServerCert;


        File file = new File("testingtransferubunt");
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];

        // Input Stream.
        InputStream in = new FileInputStream(file);

        // Output Stream.
        OutputStream out = socket.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        // Closing the streams and socket.
        out.close();
        in.close();
        socket.close();
    }
}