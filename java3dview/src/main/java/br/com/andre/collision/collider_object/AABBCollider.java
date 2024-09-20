package br.com.andre.collision.collider_object;

import br.com.andre.collision.Collider;
import br.com.andre.collision.CollisionInfo;
import br.com.andre.graphic.Vector3;

/**
 * Colisor de caixa alinhada aos eixos (AABB).
 */
public class AABBCollider implements Collider {
    private Vector3 min;
    private Vector3 max;

    public AABBCollider(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void updatePosition(Vector3 position) {
        // AABB geralmente é estático, mas se for necessário mover:
        Vector3 size = max.subtract(min);
        min = position;
        max = position.add(size);
    }

    @Override
    public Vector3 getPosition() {
        return min.add(max).multiply(0.5);
    }

    @Override
    public CollisionInfo checkCollision(Collider other) {
        if (other instanceof SphereCollider) {
            return other.checkCollision(this);
        } else if (other instanceof AABBCollider) {
            return checkCollisionWithAABB((AABBCollider) other);
        }
        return new CollisionInfo(false, null, null);
    }

    private CollisionInfo checkCollisionWithAABB(AABBCollider other) {
        if (this.max.getX() < other.min.getX() || this.min.getX() > other.max.getX()) {
            return new CollisionInfo(false, null, null);
        }
        if (this.max.getY() < other.min.getY() || this.min.getY() > other.max.getY()) {
            return new CollisionInfo(false, null, null);
        }
        if (this.max.getZ() < other.min.getZ() || this.min.getZ() > other.max.getZ()) {
            return new CollisionInfo(false, null, null);
        }

        double overlapX = Math.min(this.max.getX(), other.max.getX()) - Math.max(this.min.getX(), other.min.getX());
        double overlapY = Math.min(this.max.getY(), other.max.getY()) - Math.max(this.min.getY(), other.min.getY());
        double overlapZ = Math.min(this.max.getZ(), other.max.getZ()) - Math.max(this.min.getZ(), other.min.getZ());

        double minOverlap = overlapX;
        Vector3 collisionNormal = new Vector3(1, 0, 0);
        if (overlapY < minOverlap) {
            minOverlap = overlapY;
            collisionNormal = new Vector3(0, 1, 0);
        }
        if (overlapZ < minOverlap) {
            minOverlap = overlapZ;
            collisionNormal = new Vector3(0, 0, 1);
        }

        Vector3 centerThis = this.getPosition();
        Vector3 centerOther = other.getPosition();
        Vector3 direction = centerThis.subtract(centerOther);

        if (direction.dot(collisionNormal) < 0) {
            collisionNormal = collisionNormal.multiply(-1);
        }

        Vector3 penetrationDepth = collisionNormal.multiply(minOverlap);

        return new CollisionInfo(true, collisionNormal, penetrationDepth);
    }

    public Vector3 getClosestPoint(Vector3 point) {
        double x = Math.max(min.getX(), Math.min(point.getX(), max.getX()));
        double y = Math.max(min.getY(), Math.min(point.getY(), max.getY()));
        double z = Math.max(min.getZ(), Math.min(point.getZ(), max.getZ()));
        return new Vector3(x, y, z);
    }
}