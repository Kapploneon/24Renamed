/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class insertionSort {
    public String stringInput;
    public int[] numbersArray;
    public int heapSize;

    public insertionSort(){
        readInput();
    };

    public insertionSort(Integer[] localNumberArray){
        numbersArray = new int[localNumberArray.length];

        for(int i = 0; i < localNumberArray.length; i++)
            numbersArray[i] = localNumberArray[i];
//        System.arraycopy(localNumberArray, 0, numbersArray, 0, localNumberArray.length);
    }

    public insertionSort(String x) {
        readInput();
        Sort();
        writeOutput("insertionSort");
    }

    // Read.
    public void readInput(){
        Scanner input = null;
        try {
            input = new Scanner(new File("sortingInput"));
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

    // Write output.
    public void writeOutput(String method){

        int i = 0;
        File name = new File("SortingOutput\\"+method+"0");
        while(name.exists())
        {
            i++;
            name = new File("SortingOutput\\"+method+i);
        }

        // Create or open file.
        Formatter output = null;
        try{
            if(i>0){
                output = new Formatter("SortingOutput\\"+method+i);
            }
            else {
                output = new Formatter("SortingOutput\\"+method+"0");
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
            output.format("%s",Arrays.toString(numbersArray));
        }
        catch (FormatterClosedException formatterClosedException){
            System.err.println("Account number must be greater than 0.");
        }
        catch (NoSuchElementException elementException){
            System.err.println("Invalid input. Please try again");
        }

        // Close formatter.
        if(output != null)
            output.close();
    }

    public void Sort(){
        Sort(numbersArray);
    }

    public void Sort(int[] localNumberArray){
        // Sort.
        int key;
        int i;
        for(int j = 1;j < localNumberArray.length;j++){
            key = localNumberArray[j];
            i = j - 1;
            while(i >= 0 && localNumberArray[i] > key){
                localNumberArray[i+1] = localNumberArray[i];
                i = i - 1;
            }
            localNumberArray[i+1] = key;
        }
    }

    public void peekArray(){
        System.out.println(Arrays.toString(numbersArray));
    }
}
