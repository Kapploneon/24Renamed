package com.Algo;

public class VertexAttributes {
    public String current;     // To store the current vertex.
    public int color;       // -1 = grey; 0 = White; 1 = Black.
    public Integer distance;    // Null represent infinite.
    public String parent;
    public String[] adj;    // To store the adjacent vertices
}
