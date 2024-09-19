package br.com.andre.graphic;

public class Plane {
    public Vector3 normal;
    public double distance;

    public Plane(Vector3 normal, double distance) {
        this.normal = normal;
        this.distance = distance;
    }

    // Função para calcular a distância de um ponto ao plano
    public double distanceToPoint(Vector3 point) {
        return normal.dot(point) + distance;
    }
}