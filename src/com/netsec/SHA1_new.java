package com.netsec;

//public class SHA1 extends JFrame implements ActionListener {
public class SHA1_new {

    public class Digest {

        String digestIt(byte[] dataIn) {
            byte[] paddedData = padTheMessage(dataIn);
            int[] H = {0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0};
            int[] K = {0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6};

            if (paddedData.length % 64 != 0) {
                System.out.println("Invalid padded data length.");
                System.exit(0);
            }

            int passesReq = paddedData.length / 64;
            byte[] work = new byte[64];

            for (int passCntr = 0; passCntr < passesReq; passCntr++) {
                System.arraycopy(paddedData, 64 * passCntr, work, 0, 64);
                processTheBlock(work, H, K);
            }

            return intArrayToHexStr(H);
        }
        //-------------------------------------------//

        private byte[] padTheMessage(byte[] data) {
            int origLength = data.length;
            int tailLength = origLength % 64;
            int padLength = 0;
            if ((64 - tailLength >= 9)) {
                padLength = 64 - tailLength;
            } else {
                padLength = 128 - tailLength;
            }

            byte[] thePad = new byte[padLength];
            thePad[0] = (byte) 0x80;
            long lengthInBits = origLength * 8;

            for (int cnt = 0; cnt < 8; cnt++) {
                thePad[thePad.length - 1 - cnt] = (byte) ((lengthInBits >> (8 * cnt)) & 0x00000000000000FF);
            }

            byte[] output = new byte[origLength + padLength];

            System.arraycopy(data, 0, output, 0, origLength);
            System.arraycopy(thePad, 0, output, origLength, thePad.length);

            return output;

        }
        //-------------------------------------------//

        private void processTheBlock(byte[] work, int H[], int K[]) {

            int[] W = new int[80];
            for (int outer = 0; outer < 16; outer++) {
                int temp = 0;
                for (int inner = 0; inner < 4; inner++) {
                    temp = (work[outer * 4 + inner] & 0x000000FF) << (24 - inner * 8);
                    W[outer] = W[outer] | temp;
                }
            }

            for (int j = 16; j < 80; j++) {
                W[j] = rotateLeft(W[j - 3] ^ W[j - 8] ^ W[j - 14] ^ W[j - 16], 1);
            }

            int a = H[0];
            int b = H[1];
            int c = H[2];
            int d = H[3];
            int e = H[4];

            int temp1;
            int f;
            for (int j = 0; j < 20; j++) {
                f = (b & c) | ((~b) & d);
                //	K = 0x5A827999;
                temp1 = rotateLeft(a, 5) + f + e + K[0] + W[j];
                System.out.println(Integer.toHexString(K[0]));
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp1;
            }

            for (int j = 20; j < 40; j++) {
                f = b ^ c ^ d;
                //   K = 0x6ED9EBA1;
                temp1 = rotateLeft(a, 5) + f + e + K[1] + W[j];
                System.out.println(Integer.toHexString(K[1]));
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp1;
            }

            for (int j = 40; j < 60; j++) {
                f = (b & c) | (b & d) | (c & d);
                //   K = 0x8F1BBCDC;
                temp1 = rotateLeft(a, 5) + f + e + K[2] + W[j];
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp1;
            }

            for (int j = 60; j < 80; j++) {
                f = b ^ c ^ d;
                //   K = 0xCA62C1D6;
                temp1 = rotateLeft(a, 5) + f + e + K[3] + W[j];
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp1;
            }

            H[0] += a;
            H[1] += b;
            H[2] += c;
            H[3] += d;
            H[4] += e;

            int n;
            for (n = 0; n < 16; n++) {
                System.out.println("W[" + n + "] = " + toHexString(W[n]));
            }

            System.out.println("H0:" + Integer.toHexString(H[0]));
            System.out.println("H0:" + Integer.toHexString(H[1]));
            System.out.println("H0:" + Integer.toHexString(H[2]));
            System.out.println("H0:" + Integer.toHexString(H[3]));
            System.out.println("H0:" + Integer.toHexString(H[4]));
        }

        final int rotateLeft(int value, int bits) {
            int q = (value << bits) | (value >>> (32 - bits));
            return q;
        }
    }

    private String intArrayToHexStr(int[] data) {
        String output = "";
        String tempStr = "";
        int tempInt = 0;
        for (int cnt = 0; cnt < data.length; cnt++) {

            tempInt = data[cnt];

            tempStr = Integer.toHexString(tempInt);

            if (tempStr.length() == 1) {
                tempStr = "0000000" + tempStr;
            } else if (tempStr.length() == 2) {
                tempStr = "000000" + tempStr;
            } else if (tempStr.length() == 3) {
                tempStr = "00000" + tempStr;
            } else if (tempStr.length() == 4) {
                tempStr = "0000" + tempStr;
            } else if (tempStr.length() == 5) {
                tempStr = "000" + tempStr;
            } else if (tempStr.length() == 6) {
                tempStr = "00" + tempStr;
            } else if (tempStr.length() == 7) {
                tempStr = "0" + tempStr;
            }
            output = output + tempStr;
        }//end for loop
        return output;
    }//end intArrayToHexStr
    //-------------------------------------------//

    static final String toHexString(int x) {
        return padStr(Integer.toHexString(x));
    }
    static final String ZEROS = "00000000";

    static final String padStr(String s) {
        if (s.length() > 8) {
            return s.substring(s.length() - 8);
        }
        return ZEROS.substring(s.length()) + s;
    }

    public String filet_sha1(byte[] input){
        Digest digester = new Digest();
        String thedigest = digester.digestIt(input);
        return thedigest;
    }

}