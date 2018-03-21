/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.CodeJam;

import java.util.*;
import java.io.*;

public class GBusCount {
    public static void main(String[] args) {

//        Scanner in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        try (Scanner in = new Scanner((new File("GBusCount.in")))) {
            int t = in.nextInt();  // Scanner has functions to read ints, longs, strings, chars, etc.

            // Get the number of cases.
            for (int i = 1; i <= t; ++i) {

                // Number of buses
                int n = in.nextInt();

                int GBusRoutes[] = new int[2*n];

                int m, Gx, temp;


                // Get the Bus routes array
                for(int x = 0;x < GBusRoutes.length; x++)
                    GBusRoutes[x] = in.nextInt();

                // Get the number of outputs to compute.
                m = in.nextInt();
                String ux = "";
                for(int y = 1; y <= m; y++){

                    Gx = in.nextInt();
                    temp = 0;
                    for(int z = 0; z < 2*n; z = z + 2){
                        if(Gx >= GBusRoutes[z] && Gx <= GBusRoutes[z+1]){
                            temp++;
                        }
                    }
                    ux = ux + temp + " ";
                }
                System.out.println("Case #" + i + ": " + ux);
            }

        }catch(FileNotFoundException fileNotFoundException)
        {
            System.err.println("Error opening file.");
            System.exit(1);
        }
    }
}
