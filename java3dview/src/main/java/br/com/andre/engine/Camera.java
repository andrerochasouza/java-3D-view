package br.com.andre.engine;

import br.com.andre.graphic.Vector3;
import br.com.andre.collision.CollisionHandler;
import br.com.andre.collision.CollisionObject;
import br.com.andre.collision.CollisionResult;

import java.util.List;

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
    private final double speed;
    private final double sensitivity;
    private final double radius;

    public final Physics physics;
    private final CollisionHandler collisionHandler;

    /**
     * Inicializa a câmera na origem, olhando para o eixo -Z.
     */
    public Camera() {
        position = new Vector3(9, -9.0, 5); // Ajuste a posição inicial se necessário
        yaw = -90; // Olhando para -Z
        pitch = 0;
        speed = 0.1;
        sensitivity = 0.1;
        radius = 0.5; // Define o raio da esfera da câmera

        physics = new Physics();
        collisionHandler = new CollisionHandler(radius);

        updateVectors();
    }

    /**
     * Atualiza a câmera, aplicando física e movimentação.
     *
     * @param deltaTime        O tempo decorrido desde a última atualização (em segundos).
     * @param collisionObjects A lista de objetos para verificação de colisão.
     */
    public void update(double deltaTime, List<CollisionObject> collisionObjects) {
        physics.update(deltaTime);
        Vector3 movement = physics.getVelocity().multiply(deltaTime);
        Vector3 newPosition = position.add(movement);

        handleCollisionAndMovement(newPosition, movement, collisionObjects);
    }

    private void handleCollisionAndMovement(Vector3 newPosition, Vector3 movement, List<CollisionObject> collisionObjects) {
        CollisionResult collisionResult = collisionHandler.checkCollision(newPosition, collisionObjects);
        if (!collisionResult.collision) {
            position = newPosition;
            physics.setGrounded(false);
        } else {
            if (collisionResult.collisionNormal.getY() > 0) {
                handleGroundCollision(collisionResult);
            } else {
                position = collisionHandler.adjustMovementWithSliding(position, movement, collisionResult.collisionNormal, collisionObjects);
            }
        }
    }

    private void handleGroundCollision(CollisionResult collisionResult) {
        physics.setGrounded(true);
        position = position.setY(collisionResult.collisionPoint.getY() + radius);
    }

    /**
     * Move a câmera para frente na direção em que está olhando.
     */
    public void moveForward(double deltaTime, List<CollisionObject> collisionObjects) {
        move(direction, speed * deltaTime, collisionObjects);
    }

    /**
     * Move a câmera para trás, oposto à direção em que está olhando.
     */
    public void moveBackward(double deltaTime, List<CollisionObject> collisionObjects) {
        move(direction, -speed * deltaTime, collisionObjects);
    }

    /**
     * Move a câmera para a esquerda relativa à sua direção atual.
     */
    public void moveLeft(double deltaTime, List<CollisionObject> collisionObjects) {
        move(right, -speed * deltaTime, collisionObjects);
    }

    /**
     * Move a câmera para a direita relativa à sua direção atual.
     */
    public void moveRight(double deltaTime, List<CollisionObject> collisionObjects) {
        move(right, speed * deltaTime, collisionObjects);
    }

    private void move(Vector3 direction, double distance, List<CollisionObject> collisionObjects) {
        Vector3 movement = direction.multiply(distance);
        Vector3 newPosition = position.add(movement);

        CollisionResult collisionResult = collisionHandler.checkCollision(newPosition, collisionObjects);
        if (!collisionResult.collision) {
            position = newPosition;
        } else {
            position = collisionHandler.adjustMovementWithSliding(position, movement, collisionResult.collisionNormal, collisionObjects);
        }
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
        clampPitch();
        updateVectors();
    }

    private void clampPitch() {
        pitch = Math.max(-89.0, Math.min(89.0, pitch));
    }

    private void updateVectors() {
        direction = calculateDirection().normalize();
        right = calculateRight().normalize();
        up = right.cross(direction).normalize();
    }

    private Vector3 calculateDirection() {
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        double x = Math.cos(radYaw) * Math.cos(radPitch);
        double y = Math.sin(radPitch);
        double z = Math.sin(radYaw) * Math.cos(radPitch);

        return new Vector3(x, y, z);
    }

    private Vector3 calculateRight() {
        return new Vector3(0, 1, 0).cross(direction);
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