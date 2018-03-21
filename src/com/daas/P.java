/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

/*
P_input.in
The input file format is as follows:
1st line - the message to hash: "I Love Data Security Class So Much"
2nd line - nonce keys to use: 2,4,6,8
3rd line - number of times to execute the program with a single nonce key: 10
 */

package com.daas;

import org.apache.commons.codec.digest.DigestUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class P {
    public static void main(String[] args) {
        try (Scanner in = new Scanner((new File("\\Input\\P\\P_input.in")))) {
            String msg = in.nextLine();                 // first line - message to hash
            String nonce_string = in.nextLine();        // second line - comma separated nonce.
            int nonce_array[];
            nonce_array = Stream.of(nonce_string.split(",")).mapToInt(Integer::parseInt).toArray();
            int times_to_run = in.nextInt();            // third line - number of times to run each nonce.
            long nonce;
            String stringToHash;
            String sha256hex;
            String stringToCompare;
            long tries;
            int nonceLength;
            // to store result of each nonce which is executed times_to_run time. eg. 2 nonce and 5 runs = 10 results req to store.
            node_result node_result[] = new node_result[nonce_array.length*times_to_run];
            int newNode = 0;
            long sum_of_tries = 0;
            double sd = 0;      // Standard deviation.
            node_result_stats node_result_stats[] = new node_result_stats[nonce_array.length];
            Calendar cal = Calendar.getInstance();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            byte[] localhash;
            String bitString;


            for(int x = 0; x < nonce_array.length; x++){
                nonceLength = nonce_array[x];
                stringToCompare = "";
                node_result_stats[x] = new node_result_stats();
                for(int z = 0; z < nonceLength; z++)
                    stringToCompare = stringToCompare + "0";
                for(int y = 0; y < times_to_run; y++){

                    tries = 0;

                    do {
                        tries++;
                        nonce = new Random().nextLong();
                        stringToHash = msg + nonce;
                        sha256hex = DigestUtils.sha256Hex(stringToHash);
                        localhash = DigestUtils.sha256(stringToHash);
                        bitString = toBitString(localhash);
                    }while (! bitString.substring(0,nonceLength).equals(stringToCompare));

                    node_result[newNode] = new node_result();
                    node_result[newNode].sha256hex_comp = sha256hex;
                    node_result[newNode].nonce_lenght = nonceLength;
                    node_result[newNode].nonce_found = nonce;
                    node_result[newNode].nonce_hex = Long.toHexString(nonce);
                    node_result[newNode].tries_req = tries;
                    node_result_stats[x].sum_tries = node_result_stats[x].sum_tries + tries;
                    newNode++;
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    System.out.println("Finish generating nonce "+ nonce_array[x] +" for "+y+"th time"+" values at time "+timestamp);
                }

            }

            // Writing to file.
            int i = 0;
            File name = new File("OutPut\\P\\"+"p"+"0");
            while(name.exists())
            {
                i++;
                name = new File("OutPut\\P\\"+"p"+i);
            }

            // Create or open file.
            Formatter output = null;
            try{
                if(i>0){
                    output = new Formatter("OutPut\\P\\"+"p"+i);
                }
                else {
                    output = new Formatter("OutPut\\P\\"+"p"+"0");
                }
            }
            catch (SecurityException securityException)
            {
                System.err.println("You do not have write access to this file.");
                System.exit(1);
            }
            catch (FileNotFoundException fileNotFoundException){
                System.err.println("Error opening or creating file.");
                System.exit(1);
            }

            // Write the output to file.
            try {
                for(int x = 0; x < node_result.length; x++) {
                    output.format("%s" + "," + "%s" + "," + "%d" + "\r\n", node_result[x].sha256hex_comp, node_result[x].nonce_hex, node_result[x].tries_req);
                }

                for(int y = 0; y < node_result_stats.length; y++) {
                    node_result_stats[y].avg_tries = (1.0d * node_result_stats[y].sum_tries) / times_to_run;
                    output.format("\r\n Average tries for difficulty of length %d: %f", nonce_array[y], node_result_stats[y].avg_tries);
                }

                newNode = 0;
                double variance = 0;
                double local_var = 0;
                for(int y = 0; y < node_result_stats.length; y++){

                    sd = 0;
                    for (int x = 0; x < times_to_run;x++)
                    {
                        local_var =  Math.pow(node_result[newNode].tries_req - node_result_stats[y].avg_tries, 2);
//                        output.format("\r\n Variance for %d is %f",node_result[newNode].tries_req, local_var);
                        sd = sd + local_var;
                        newNode++;
                    }
                    variance = sd / times_to_run;
                    node_result_stats[y].sd_tries =  Math.sqrt(variance);
//                    output.format("\r\n Variance of tries for difficulty of length %d : %f", nonce_array[y], variance);
                    output.format("\r\n Standard deviation of tries for difficulty of length %d : %f", nonce_array[y], node_result_stats[y].sd_tries);

                }
            }
            catch (FormatterClosedException formatterClosedException){
                System.err.println("Account number must be greater than 0.");
            }
            catch (NoSuchElementException elementException){
                System.err.println("Invalid input. Please try again");
            }

            System.out.println("Finished!!");

            // Close formatter.
            if(output != null)
                output.close();

        }catch(FileNotFoundException fileNotFoundException)
        {
            System.err.println("Error opening file.");
            System.exit(1);
        }

    }

    public static String toBitString(final byte[] b) {
        final char[] bits = new char[8 * b.length];
        for(int i = 0; i < b.length; i++) {
            final byte byteval = b[i];
            int bytei = i << 3;
            int mask = 0x1;
            for(int j = 7; j >= 0; j--) {
                final int bitval = byteval & mask;
                if(bitval == 0) {
                    bits[bytei + j] = '0';
                } else {
                    bits[bytei + j] = '1';
                }
                mask <<= 1;
            }
        }
        return String.valueOf(bits);
    }

    static class node_result{
        String sha256hex_comp;
        long tries_req;
        long nonce_found;
        String nonce_hex;
        int nonce_lenght;
    }
    static class node_result_stats{
        double avg_tries;
        double sd_tries;
        double sum_tries;
    }

}
