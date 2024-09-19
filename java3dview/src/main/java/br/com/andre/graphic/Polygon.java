package br.com.andre.graphic;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Polygon {
    private Color color;
    private List<Vector3> vertices;
    private boolean cullBackFace;

    public Polygon(Color color, boolean cullBackFace, Vector3... vertices) {
        this.color = color;
        this.vertices = Arrays.asList(vertices);
        this.cullBackFace = cullBackFace;
    }

    public Color getColor() {
        return color;
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public boolean isCullBackFace() {
        return cullBackFace;
    }
}