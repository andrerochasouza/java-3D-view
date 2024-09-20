package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

/**
 * CollisionDetector lida com a detecção de colisões entre diferentes formas.
 */
public class CollisionDetector {

    /**
     * Verifica se uma esfera intersecta uma AABB (Axis-Aligned Bounding Box).
     *
     * @param sphereCenter A posição central da esfera.
     * @param sphereRadius O raio da esfera.
     * @param aabbMin      O canto mínimo da AABB.
     * @param aabbMax      O canto máximo da AABB.
     * @return O resultado da colisão, incluindo a normal da colisão e o ponto de colisão.
     */
    public static CollisionResult sphereIntersectsAABB(Vector3 sphereCenter, double sphereRadius, Vector3 aabbMin, Vector3 aabbMax) {
        // Encontra o ponto mais próximo na AABB para o centro da esfera
        double closestX = clamp(sphereCenter.getX(), aabbMin.getX(), aabbMax.getX());
        double closestY = clamp(sphereCenter.getY(), aabbMin.getY(), aabbMax.getY());
        double closestZ = clamp(sphereCenter.getZ(), aabbMin.getZ(), aabbMax.getZ());

        Vector3 closestPoint = new Vector3(closestX, closestY, closestZ);

        // Calcula a distância entre o ponto mais próximo e o centro da esfera
        Vector3 difference = sphereCenter.subtract(closestPoint);
        double distanceSquared = difference.lengthSquared();

        if (distanceSquared <= sphereRadius * sphereRadius) {
            double distance = Math.sqrt(distanceSquared);
            // Evita divisão por zero
            if (distance == 0) {
                // Colisão direta no centro da AABB, normal arbitrária
                return new CollisionResult(true, closestPoint, new Vector3(0, 1, 0));
            }
            Vector3 collisionNormal = difference.divide(distance); // Normal normalizada
            return new CollisionResult(true, closestPoint, collisionNormal);
        }

        return new CollisionResult(false, null, null);
    }

    /**
     * Função auxiliar para limitar um valor entre mínimo e máximo.
     *
     * @param value O valor a ser limitado.
     * @param min   O valor mínimo.
     * @param max   O valor máximo.
     * @return O valor limitado.
     */
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}