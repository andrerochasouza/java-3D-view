package br.com.andre.graphic;

import java.awt.*;
import java.util.List;

/**
 * Classe interna para representar um polígono pronto para renderização.
 */
record RenderedPolygon(List<Vector3> vertices, Color color, double depth) { }