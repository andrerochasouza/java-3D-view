package br.com.andre.engine;

import br.com.andre.graphic.Vector3;
import br.com.andre.collision.CollisionDetector;
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
    private double speed;
    private double sensitivity;
    private double radius;

    Physics physics;

    /**
     * Inicializa a câmera na origem, olhando para o eixo -Z.
     */
    public Camera() {
        position = new Vector3(9, -9.0, 5); // Ajuste a posição inicial se necessário
        direction = new Vector3(0, 0, -1);
        up = new Vector3(0, 1, 0);
        right = new Vector3(1, 0, 0);
        yaw = -90; // Olhando para -Z
        pitch = 0;
        speed = 0.1;
        sensitivity = 0.1;
        radius = 0.5; // Define o raio da esfera da câmera
        physics = new Physics();
    }

    /**
     * Atualiza a câmera, aplicando física e movimentação.
     *
     * @param deltaTime         O tempo decorrido desde a última atualização (em segundos).
     * @param collisionObjects  A lista de objetos para verificação de colisão.
     */
    public void update(double deltaTime, List<CollisionObject> collisionObjects) {
        // Atualiza a física
        physics.update(deltaTime);

        // Calcula a nova posição com base na física
        Vector3 movement = physics.getVelocity().multiply(deltaTime);
        Vector3 newPosition = position.add(movement);

        // Verifica colisões
        CollisionResult collisionResult = checkCollision(newPosition, collisionObjects);
        if (!collisionResult.collision) {
            position = newPosition;
            physics.setGrounded(false);
        } else {
            // Se colidir com o chão, define como grounded
            if (collisionResult.collisionNormal.getY() > 0) {
                physics.setGrounded(true);
                position = position.setY(collisionResult.collisionPoint.getY() + radius);
            } else {
                // Ajusta movimento com deslizamento
                adjustMovementWithSliding(movement, collisionResult.collisionNormal, collisionObjects);
            }
        }
    }

    /**
     * Move a câmera para frente na direção em que está olhando.
     */
    public void moveForward(double deltaTime, List<CollisionObject> collisionObjects) {
        Vector3 movement = direction.multiply(speed * deltaTime);
        move(movement, collisionObjects);
    }

    /**
     * Move a câmera para trás, oposto à direção em que está olhando.
     */
    public void moveBackward(double deltaTime, List<CollisionObject> collisionObjects) {
        Vector3 movement = direction.multiply(-speed * deltaTime);
        move(movement, collisionObjects);
    }

    /**
     * Move a câmera para a esquerda relativa à sua direção atual.
     */
    public void moveLeft(double deltaTime, List<CollisionObject> collisionObjects) {
        Vector3 movement = right.multiply(-speed * deltaTime);
        move(movement, collisionObjects);
    }

    /**
     * Move a câmera para a direita relativa à sua direção atual.
     */
    public void moveRight(double deltaTime, List<CollisionObject> collisionObjects) {
        Vector3 movement = right.multiply(speed * deltaTime);
        move(movement, collisionObjects);
    }

    private void move(Vector3 movement, List<CollisionObject> collisionObjects) {
        Vector3 newPosition = position.add(movement);
        CollisionResult collisionResult = checkCollision(newPosition, collisionObjects);
        if (!collisionResult.collision) {
            position = newPosition;
        } else {
            adjustMovementWithSliding(movement, collisionResult.collisionNormal, collisionObjects);
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

        // Limita o pitch para evitar que a câmera vire
        if (pitch > 89.0) pitch = 89.0;
        if (pitch < -89.0) pitch = -89.0;

        updateVectors();
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

    // Atualize o metodo checkCollision para retornar o ponto de colisão
    private CollisionResult checkCollision(Vector3 newPosition, List<CollisionObject> collisionObjects) {
        for (CollisionObject obj : collisionObjects) {
            CollisionObject.AABB boundingBox = obj.getBoundingBox();
            CollisionResult result = CollisionDetector.sphereIntersectsAABB(newPosition, radius, boundingBox.getMin(), boundingBox.getMax());
            if (result.collision) {
                return result;
            }
        }
        return new CollisionResult(false, null, null);
    }

    private void adjustMovementWithSliding(Vector3 movement, Vector3 collisionNormal, List<CollisionObject> collisionObjects) {
        // Remove o componente do movimento na direção da normal da colisão
        Vector3 movementParallel = movement.subtract(collisionNormal.multiply(movement.dot(collisionNormal)));

        // Tenta mover na direção ajustada
        Vector3 newPosition = position.add(movementParallel);

        CollisionResult collisionResult = checkCollision(newPosition, collisionObjects);
        if (!collisionResult.collision) {
            position = newPosition;
        }
    }
}