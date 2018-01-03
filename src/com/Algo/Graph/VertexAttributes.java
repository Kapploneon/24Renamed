/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Graph;

public class VertexAttributes {
    public String current;     // To store the current vertex.
    public int color;       // -1 = grey; 0 = White; 1 = Black.
    public Integer distance;    // Null represent infinite.
    public String parent;
    public String[] adj;    // To store the adjacent vertices
}
