package br.com.andre.graphic;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PolygonGraphic {
    private String groupName;
    private List<Vector3> vertices;
    private Color color;
    private boolean cullBackFace;

    public PolygonGraphic(String groupName, Color color, boolean cullBackFace, Vector3... vertices) {
        this.groupName = groupName;
        this.color = color;
        this.cullBackFace = cullBackFace;
        this.vertices = Arrays.asList(vertices);
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public Color getColor() {
        return color;
    }

    public boolean isCullBackFace() {
        return cullBackFace;
    }
}