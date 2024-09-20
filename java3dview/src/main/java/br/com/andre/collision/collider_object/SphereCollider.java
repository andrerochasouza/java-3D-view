package br.com.andre.collision.collider_object;

import br.com.andre.collision.Collider;
import br.com.andre.collision.CollisionInfo;
import br.com.andre.graphic.Vector3;

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
        return new CollisionInfo(false, null, null);
    }

    private CollisionInfo checkCollisionWithSphere(SphereCollider other) {
        Vector3 difference = other.position.subtract(this.position);
        double distanceSquared = difference.lengthSquared();
        double radiusSum = this.radius + other.radius;
        if (distanceSquared <= radiusSum * radiusSum) {
            double distance = Math.sqrt(distanceSquared);
            Vector3 collisionNormal = difference.divide(distance);
            Vector3 penetrationDepth = collisionNormal.multiply(radiusSum - distance);
            return new CollisionInfo(true, collisionNormal, penetrationDepth);
        }
        return new CollisionInfo(false, null, null);
    }

    private CollisionInfo checkCollisionWithAABB(AABBCollider other) {
        Vector3 closestPoint = other.getClosestPoint(position);
        Vector3 difference = position.subtract(closestPoint);
        double distanceSquared = difference.lengthSquared();

        if (distanceSquared <= radius * radius) {
            double distance = Math.sqrt(distanceSquared);
            if (distance == 0) {
                return new CollisionInfo(true, new Vector3(0, 1, 0), new Vector3(0, radius, 0));
            }
            Vector3 collisionNormal = difference.divide(distance);
            Vector3 penetrationDepth = collisionNormal.multiply(radius - distance);
            return new CollisionInfo(true, collisionNormal, penetrationDepth);
        }
        return new CollisionInfo(false, null, null);
    }
}