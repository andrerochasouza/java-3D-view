package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

import java.util.List;

/**
 * CollisionHandler lida com detecção e resolução de colisões.
 */
public class CollisionHandler {

    private final double radius;

    public CollisionHandler(double radius) {
        this.radius = radius;
    }

    /**
     * Verifica se há colisão na nova posição.
     *
     * @param newPosition      A nova posição da câmera.
     * @param collisionObjects A lista de objetos para verificação de colisão.
     * @return O resultado da colisão.
     */
    public CollisionResult checkCollision(Vector3 newPosition, List<CollisionObject> collisionObjects) {
        for (CollisionObject obj : collisionObjects) {
            CollisionObject.AABB boundingBox = obj.getBoundingBox();
            CollisionResult result = CollisionDetector.sphereIntersectsAABB(
                    newPosition, radius, boundingBox.getMin(), boundingBox.getMax());
            if (result.collision) {
                return result;
            }
        }
        return new CollisionResult(false, null, null);
    }

    /**
     * Ajusta o movimento da câmera em caso de colisão, permitindo deslizamento.
     *
     * @param position         A posição atual da câmera.
     * @param movement         O vetor de movimento desejado.
     * @param collisionNormal  A normal da colisão.
     * @param collisionObjects A lista de objetos para verificação de colisão.
     * @return A nova posição ajustada.
     */
    public Vector3 adjustMovementWithSliding(Vector3 position, Vector3 movement, Vector3 collisionNormal, List<CollisionObject> collisionObjects) {
        Vector3 movementParallel = movement.subtract(collisionNormal.multiply(movement.dot(collisionNormal)));
        Vector3 newPosition = position.add(movementParallel);

        CollisionResult collisionResult = checkCollision(newPosition, collisionObjects);
        return collisionResult.collision ? position : newPosition;
    }
}