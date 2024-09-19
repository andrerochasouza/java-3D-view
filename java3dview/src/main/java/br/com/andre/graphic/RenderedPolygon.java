package br.com.andre.graphic;

import java.awt.*;
import java.util.List;

class RenderedPolygon {
    private List<Vector3> vertices;
    private Color color;
    private double depth;

    /**
     * Construtor que cria um RenderedPolygon com os vértices, cor e profundidade fornecidos.
     *
     * @param vertices a lista de vértices projetados
     * @param color    a cor do polígono
     * @param depth    a profundidade média do polígono
     */
    public RenderedPolygon(List<Vector3> vertices, Color color, double depth) {
        this.vertices = vertices;
        this.color = color;
        this.depth = depth;
    }

    /**
     * Obtém os vértices projetados do polígono.
     *
     * @return a lista de vértices
     */
    public List<Vector3> getVertices() {
        return vertices;
    }

    /**
     * Obtém a cor do polígono.
     *
     * @return a cor
     */
    public Color getColor() {
        return color;
    }

    /**
     * Obtém a profundidade média do polígono.
     *
     * @return a profundidade
     */
    public double getDepth() {
        return depth;
    }
}