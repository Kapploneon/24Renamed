package com.java24hours;

import com.Algo.VertexAttributes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BreathFirstSearch {

    public BreathFirstSearch() {
        // Do nothing.
    }

    public static void main(String arg[]){
        // Here is the main function which will execute BreathFirstSearch algorithm.

        Graph graph = new Graph();
        ReadGraph rg = new ReadGraph();

        rg.ReadGraph();
        graph = rg.getGraph();

        // BreathFirstSearch BFS = new BreathFirstSearch();
        Queue<String> q = new LinkedList<>();
        VertexAttributes[] VA = new VertexAttributes[graph.gV.length];
        int vertuPos;
        int vertAdjPos;
        String bfstraversal;

        // Initialize
        for (int x = 0; x < graph.gV.length; x++)
        {
            VA[x] = new VertexAttributes();
            VA[x].color = 0;
            VA[x].distance = null;
            VA[x].parent = null;
            VA[x].current = graph.gV[x];
            VA[x].adj = graph.Edges[x].split(",");
        }

        VA[0].color = -1;
        VA[0].distance = 0;
        VA[0].parent = null;
        q.add(graph.gV[0]);             // Enqueue
        bfstraversal = graph.gV[0];

        while (q.peek() != null)
        {
            String u = q.remove();      // Dequeue
            vertuPos = Arrays.asList(graph.gV).indexOf(u);
            for( int forloo = 0; forloo < VA[vertuPos].adj.length; forloo++ )
            {
                vertAdjPos = Arrays.asList(graph.gV).indexOf(VA[vertuPos].adj[forloo]);
                if(VA[vertAdjPos].color == 0)
                {
                    VA[vertAdjPos].color = -1;
                    VA[vertAdjPos].distance = VA[vertuPos].distance + 1;
                    VA[vertAdjPos].parent = u;
                    q.add(VA[vertAdjPos].current);
                    bfstraversal = bfstraversal + VA[vertAdjPos].current;
                }
            }
            VA[vertuPos].color = 1;
        }

        System.out.printf("%s",bfstraversal);
    }
}
