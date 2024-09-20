package br.com.andre.object;

import br.com.andre.graphic.Vector3;

public class CollisionDetector {

    /**
     * Verifica se uma esfera colide com um AABB.
     *
     * @param sphereCenter Centro da esfera.
     * @param radius        Raio da esfera.
     * @param boxMin        Mínimos do AABB.
     * @param boxMax        Máximos do AABB.
     * @return Resultado da colisão.
     */
    public static CollisionResult sphereIntersectsAABB(Vector3 sphereCenter, double radius, Vector3 boxMin, Vector3 boxMax) {
        Vector3 closestPoint = new Vector3(
                clamp(sphereCenter.getX(), boxMin.getX(), boxMax.getX()),
                clamp(sphereCenter.getY(), boxMin.getY(), boxMax.getY()),
                clamp(sphereCenter.getZ(), boxMin.getZ(), boxMax.getZ())
        );

        Vector3 difference = sphereCenter.subtract(closestPoint);
        double distanceSquared = difference.lengthSquared();

        boolean collision = distanceSquared < radius * radius;

        Vector3 collisionNormal = null;
        if (collision) {
            collisionNormal = difference.normalize();
        }

        return new CollisionResult(collision, collisionNormal, closestPoint);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}