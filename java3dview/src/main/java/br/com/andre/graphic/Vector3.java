package br.com.andre.graphic;

public class Vector3 {
    private double x;
    private double y;
    private double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    // Soma de vetores
    public Vector3 add(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    // Subtração de vetores
    public Vector3 subtract(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    // Multiplicação por um escalar
    public Vector3 multiply(double scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    // Produto vetorial
    public Vector3 cross(Vector3 other) {
        double newX = this.y * other.z - this.z * other.y;
        double newY = this.z * other.x - this.x * other.z;
        double newZ = this.x * other.y - this.y * other.x;
        return new Vector3(newX, newY, newZ);
    }

    // Produto escalar
    public double dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    // Normalização do vetor
    public Vector3 normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length == 0) {
            return new Vector3(0, 0, 0);
        }
        return new Vector3(x / length, y / length, z / length);
    }

    // Verifica se o vetor é igual a outro
    public boolean equals(Vector3 other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    // Representação em string para depuração
    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}