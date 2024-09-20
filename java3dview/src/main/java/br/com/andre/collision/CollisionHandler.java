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
     * @param newPosition      A nova posição do jogador.
     * @param collisionObjects A lista de objetos para verificação de colisão.
     * @return O resultado da colisão.
     */
    public CollisionResult checkCollision(Vector3 newPosition, List<CollisionObject> collisionObjects) {
        for (CollisionObject obj : collisionObjects) {
            CollisionObject.AABB boundingBox = obj.getBoundingBox();
            CollisionResult result = CollisionDetector.sphereIntersectsAABB(
                    newPosition, radius, boundingBox.getMin(), boundingBox.getMax());
            if (result.collision) {
                // Log de depuração
                System.out.println("Colisão detectada com AABB: " + obj.getName());
                System.out.println("Ponto de colisão: " + result.collisionPoint);
                System.out.println("Normal da colisão: " + result.collisionNormal);
                return result;
            }
        }
        return new CollisionResult(false, null, null);
    }

    /**
     * Ajusta o movimento do jogador em caso de colisão, permitindo deslizamento.
     *
     * @param position         A posição atual do jogador.
     * @param movement         O vetor de movimento desejado.
     * @param collisionNormal  A normal da colisão.
     * @param collisionObjects A lista de objetos para verificação de colisão.
     * @return A nova posição ajustada.
     */
    public Vector3 adjustMovementWithSliding(Vector3 position, Vector3 movement, Vector3 collisionNormal, List<CollisionObject> collisionObjects) {
        Vector3 movementParallel = movement.subtract(collisionNormal.multiply(movement.dot(collisionNormal)));
        Vector3 newPosition = position.add(movementParallel);

        CollisionResult collisionResult = checkCollision(newPosition, collisionObjects);
        if (collisionResult.collision) {
            // Log de depuração
            System.out.println("Deslizamento detectado. Ajustando movimento.");
            System.out.println("Normal da colisão durante deslizamento: " + collisionResult.collisionNormal);
            return position; // Mantém a posição atual se ainda houver colisão
        }

        return newPosition;
    }
}