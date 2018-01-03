/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Graph;

import com.java24hours.Graph;

import java.util.Arrays;

public class DFS {

    public String visit_path;

    void DFS_VISIT(Graph g, VertexAttributes[] va, int i)
    {
        int findAdj = 0;

        va[i].color = -1;
        for (int x = 0; x < va[i].adj.length; x++) {
            findAdj = Arrays.asList(g.gV).indexOf(va[i].adj[x]);
            if (va[findAdj].color == 0)
            {
                visit_path = visit_path + va[findAdj].current;
                DFS_VISIT(g, va, findAdj);
            }
        }
        va[i].color = 1;
    }

    public static void main(String arg[]){

        Graph graph = new Graph();
        ReadGraph rg = new ReadGraph();
        DFS dfsObject = new DFS();
        rg.ReadGraph();
        graph = rg.getGraph();

        // BreathFirstSearch BFS = new BreathFirstSearch();
        //Queue<String> q = new LinkedList<>();
        VertexAttributes[] VA = new VertexAttributes[graph.gV.length];
        int vertuPos;
        int vertAdjPos;
        String dfstraversal;

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

        for ( int i = 0; i < VA.length; i++)
        {
            if (VA[i].color == 0) {
                dfsObject.visit_path = VA[i].current;
                dfsObject.DFS_VISIT(graph, VA, i);
                System.out.printf("%s",dfsObject.visit_path);
            }
        }
    }
}
