package br.com.andre.util;

import br.com.andre.graphic.PolygonGraphic;
import br.com.andre.graphic.Vector3;

import java.util.List;

public class CalcPolygon {

    /**
     * Calcula o vetor normal de um polígono.
     *
     * @param polygonGraphic o polígono para o qual calcular a normal
     * @return o vetor normal normalizado do polígono
     */
    public static Vector3 calculatePolygonNormal(PolygonGraphic polygonGraphic) {
        List<Vector3> vertices = polygonGraphic.getVertices();

        Vector3 v0 = vertices.get(0);
        Vector3 v1 = vertices.get(1);
        Vector3 v2 = vertices.get(2);

        Vector3 edge1 = v1.subtract(v0);
        Vector3 edge2 = v2.subtract(v0);

        return edge1.cross(edge2).normalize();
    }

    /**
     * Calcula o centro de um polígono.
     *
     * @param polygonGraphic o polígono para o qual calcular o centro
     * @return o vetor representando o centro do polígono
     */
    public static Vector3 calculatePolygonCenter(PolygonGraphic polygonGraphic) {
        List<Vector3> vertices = polygonGraphic.getVertices();
        double x = 0, y = 0, z = 0;
        int numVertices = vertices.size();

        for (Vector3 vertex : vertices) {
            x += vertex.getX();
            y += vertex.getY();
            z += vertex.getZ();
        }

        return new Vector3(x / numVertices, y / numVertices, z / numVertices);
    }
}
