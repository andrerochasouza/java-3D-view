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
     * @return true se houver colisão, false caso contrário.
     */
    public static CollisionResult sphereIntersectsAABB(Vector3 sphereCenter, double radius, Vector3 boxMin, Vector3 boxMax) {
        double distanceSquared = 0;
        Vector3 closestPoint = new Vector3(0, 0, 0);

        // Eixo X
        double sphereX = sphereCenter.getX();
        double minX = boxMin.getX();
        double maxX = boxMax.getX();
        double closestX = Math.max(minX, Math.min(sphereX, maxX));
        double distanceX = sphereX - closestX;
        distanceSquared += distanceX * distanceX;
        closestPoint = closestPoint.setX(closestX);

        // Eixo Y
        double sphereY = sphereCenter.getY();
        double minY = boxMin.getY();
        double maxY = boxMax.getY();
        double closestY = Math.max(minY, Math.min(sphereY, maxY));
        double distanceY = sphereY - closestY;
        distanceSquared += distanceY * distanceY;
        closestPoint = closestPoint.setY(closestY);

        // Eixo Z
        double sphereZ = sphereCenter.getZ();
        double minZ = boxMin.getZ();
        double maxZ = boxMax.getZ();
        double closestZ = Math.max(minZ, Math.min(sphereZ, maxZ));
        double distanceZ = sphereZ - closestZ;
        distanceSquared += distanceZ * distanceZ;
        closestPoint = closestPoint.setZ(closestZ);

        boolean collision = distanceSquared < radius * radius;

        Vector3 collisionNormal = null;
        if (collision) {
            // Calcula a normal da colisão
            collisionNormal = sphereCenter.subtract(closestPoint).normalize();
        }

        return new CollisionResult(collision, collisionNormal);
    }
}