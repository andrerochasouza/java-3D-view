package br.com.andre.collision.collider_object;

import br.com.andre.collision.Collider;
import br.com.andre.collision.CollisionInfo;
import br.com.andre.graphic.Vector3;

/**
 * Colisor de esfera para detecção de colisões.
 */
public class SphereCollider implements Collider {
    private Vector3 position;
    private double radius;

    public SphereCollider(Vector3 position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    @Override
    public void updatePosition(Vector3 position) {
        this.position = position;
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public CollisionInfo checkCollision(Collider other) {
        if (other instanceof SphereCollider) {
            return checkCollisionWithSphere((SphereCollider) other);
        } else if (other instanceof AABBCollider) {
            return checkCollisionWithAABB((AABBCollider) other);
        }
        // Implementar outros tipos de colisores se necessário
        return new CollisionInfo(false, null, null);
    }

    private CollisionInfo checkCollisionWithSphere(SphereCollider other) {
        Vector3 delta = this.position.subtract(other.position);
        double distanceSquared = delta.lengthSquared();
        double radiusSum = this.radius + other.radius;

        if (distanceSquared > radiusSum * radiusSum) {
            return new CollisionInfo(false, null, null);
        }

        double distance = Math.sqrt(distanceSquared);
        Vector3 normal = distance > 0 ? delta.divide(distance) : new Vector3(1, 0, 0);
        double penetration = radiusSum - distance;
        Vector3 penetrationDepth = normal.multiply(penetration);

        return new CollisionInfo(true, normal, penetrationDepth);
    }

    private CollisionInfo checkCollisionWithAABB(AABBCollider aabb) {
        Vector3 closestPoint = aabb.getClosestPoint(this.position);
        Vector3 delta = this.position.subtract(closestPoint);
        double distanceSquared = delta.lengthSquared();

        if (distanceSquared > this.radius * this.radius) {
            return new CollisionInfo(false, null, null);
        }

        double distance = Math.sqrt(distanceSquared);
        Vector3 normal = distance > 0 ? delta.divide(distance) : new Vector3(1, 0, 0);
        double penetration = this.radius - distance;
        Vector3 penetrationDepth = normal.multiply(penetration);

        return new CollisionInfo(true, normal, penetrationDepth);
    }
}