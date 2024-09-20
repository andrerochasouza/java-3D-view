package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

/**
 * Representa o resultado de uma detecção de colisão.
 */
public class CollisionResult {
    public final boolean collision;
    public final Vector3 collisionPoint;
    public final Vector3 collisionNormal;

    public CollisionResult(boolean collision, Vector3 collisionPoint, Vector3 collisionNormal) {
        this.collision = collision;
        this.collisionPoint = collisionPoint;
        this.collisionNormal = collisionNormal;
    }
}