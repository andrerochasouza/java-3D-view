package br.com.andre.engine;

import br.com.andre.graphic.Vector3;

/**
 * Representa uma câmera no espaço 3D, lidando com posição e orientação.
 */
public class Camera {
    private Vector3 position;
    private Vector3 direction;
    private Vector3 up;
    private Vector3 right;
    private double yaw;
    private double pitch;
    private double speed;
    private double sensitivity;

    /**
     * Inicializa a câmera na origem, olhando para o eixo -Z.
     */
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

    /**
     * Move a câmera para frente na direção em que está olhando.
     */
    public void moveForward() {
        position = position.add(direction.multiply(speed));
    }

    /**
     * Move a câmera para trás, oposto à direção em que está olhando.
     */
    public void moveBackward() {
        position = position.subtract(direction.multiply(speed));
    }

    /**
     * Move a câmera para a esquerda relativa à sua direção atual.
     */
    public void moveLeft() {
        position = position.subtract(right.multiply(speed));
    }

    /**
     * Move a câmera para a direita relativa à sua direção atual.
     */
    public void moveRight() {
        position = position.add(right.multiply(speed));
    }

    /**
     * Rotaciona a câmera com base nos deltas de movimento do mouse.
     *
     * @param deltaX a mudança no eixo X (movimento do mouse)
     * @param deltaY a mudança no eixo Y (movimento do mouse)
     */
    public void rotate(double deltaX, double deltaY) {
        yaw += deltaX * sensitivity;
        pitch -= deltaY * sensitivity;

        // Limita o pitch para evitar que a câmera vire
        if (pitch > 89.0) pitch = 89.0;
        if (pitch < -89.0) pitch = -89.0;

        updateVectors();
    }

    /**
     * Atualiza os vetores de direção, direita e cima da câmera com base nos ângulos yaw e pitch atuais.
     */
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

    /**
     * Obtém a posição atual da câmera.
     *
     * @return o vetor posição
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Obtém a direção atual para a qual a câmera está olhando.
     *
     * @return o vetor direção
     */
    public Vector3 getDirection() {
        return direction;
    }

    /**
     * Obtém o vetor "up" (cima) da câmera.
     *
     * @return o vetor up
     */
    public Vector3 getUp() {
        return up;
    }

    /**
     * Obtém o vetor "right" (direita) da câmera.
     *
     * @return o vetor right
     */
    public Vector3 getRight() {
        return right;
    }
}