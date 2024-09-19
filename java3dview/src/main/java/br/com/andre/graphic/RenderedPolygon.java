package br.com.andre.graphic;

import java.awt.*;
import java.util.List;

class RenderedPolygon {
    private List<Vector3> vertices;
    private Color color;
    private double depth;

    public RenderedPolygon(List<Vector3> vertices, Color color, double depth) {
        this.vertices = vertices;
        this.color = color;
        this.depth = depth;
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public Color getColor() {
        return color;
    }

    public double getDepth() {
        return depth;
    }
}