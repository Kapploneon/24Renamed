/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.Stream;

public class inputReader {

    public String stringInput;
    public int[] numbersArray;

    public inputReader(){
        new inputReader("sortingInput");
    }

    public inputReader(String filename){
        Scanner input = null;
        try {
            input = new Scanner(new File(filename));
        }
        catch(FileNotFoundException fileNotFoundException)
        {
            System.err.println("Error opening file.");
            System.exit(1);
        }

        if(input.hasNext()) {
            stringInput = input.next();
            numbersArray = Stream.of(stringInput.split(",")).mapToInt(Integer::parseInt).toArray();
        }
    }
}
