package br.com.andre.engine;

import br.com.andre.graphic.Vector3;

public class Camera {
    private Vector3 position;
    private Vector3 direction;
    private Vector3 up;
    private Vector3 right;
    private double yaw;
    private double pitch;
    private double speed;
    private double sensitivity;

    public Camera() {
        position = new Vector3(0, 0, 0);
        direction = new Vector3(0, 0, -1);
        up = new Vector3(0, 1, 0);
        right = new Vector3(1, 0, 0);
        yaw = -90; // Olhando para -Z
        pitch = 0;
        speed = 0.1;
        sensitivity = 0.1;
    }

    public void moveForward() {
        position = position.add(direction.multiply(speed));
    }

    public void moveBackward() {
        position = position.subtract(direction.multiply(speed));
    }

    public void moveLeft() {
        position = position.subtract(right.multiply(speed));
    }

    public void moveRight() {
        position = position.add(right.multiply(speed));
    }

    public void rotate(double deltaX, double deltaY) {
        yaw += deltaX * sensitivity;
        pitch -= deltaY * sensitivity;

        // Limita o pitch para evitar que a câmera "gire"
        if (pitch > 89.0) pitch = 89.0;
        if (pitch < -89.0) pitch = -89.0;

        updateVectors();
    }

    private void updateVectors() {
        // Calcula o novo vetor de direção
        direction = new Vector3(
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                Math.sin(Math.toRadians(pitch)),
                Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
        ).normalize();

        // Recalcula o vetor right
        right = new Vector3(0, 1, 0).cross(direction).normalize();

        // Recalcula o vetor up
        up = right.cross(direction).normalize();
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public Vector3 getUp() {
        return up;
    }

    public Vector3 getRight() {
        return right;
    }
}