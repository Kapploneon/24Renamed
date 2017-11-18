package com.Algo;

import com.java24hours.Graph;

import java.io.*;
import java.util.Scanner;

/* The purpose of this class is to read the file and set the graph. */
/* The format used is the adjacency-list. */

public class ReadGraph {

    public Graph graph = new Graph();

    public void ReadGraph() {

        System.out.print("Enter file name which contains adjacency list of Graph: ");

        try {

            FileReader fileReader;
            Scanner filename = new Scanner(System.in);
            String line = null;
            Boolean firstLine = true;       // To read the vertices.
            int count = 0;

            fileReader = new FileReader(filename.nextLine());

            // Wrapping the FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                if(line.length() == 0)
                    continue;
                // Ignore comments and consider only those lines which are not comments
                if(!line.startsWith("#")){
                    if(line.contains("#")){
                        String[] input = line.split("#.*$");
                        if(firstLine) {
                            graph.gV = input[0].split(",");
                            firstLine = false;
                        }
                        else
                            graph.Edges = input[0].split(";");
                    }
                    else {
                        if(firstLine) {
                            graph.gV = line.split(",");
                            firstLine = false;
                        }
                        else
                            graph.Edges = line.split(";");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error opening file.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Graph getGraph(){

        return graph;  // Returning the Graph

    }
}
