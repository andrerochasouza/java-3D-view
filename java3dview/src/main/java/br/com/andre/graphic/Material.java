package br.com.andre.graphic;

import java.awt.Color;

public class Material {
    private String name;
    private Color diffuseColor;
    private boolean cullBackFace;

    public Material(String name, Color diffuseColor, boolean cullBackFace) {
        this.name = name;
        this.diffuseColor = diffuseColor;
        this.cullBackFace = cullBackFace;
    }

    public boolean isCullBackFace() {
        return cullBackFace;
    }

    public String getName() {
        return name;
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    public void setCullBackFace(boolean cullBackFace) {
        this.cullBackFace = cullBackFace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
}