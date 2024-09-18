package br.com.andre.graphic;

import java.awt.Color;
import java.util.List;

public class Polygon {
    private List<Vector3> vertices;
    private Color color;

    public Polygon(Color color, Vector3... vertices) {
        this.vertices = List.of(vertices);
        this.color = color;
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public Color getColor() {
        return color;
    }
}