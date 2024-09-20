package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

/**
 * Contém informações sobre uma colisão detectada.
 */
public class CollisionInfo {
    private boolean hasCollision;
    private Vector3 collisionNormal;
    private Vector3 penetrationDepth;

    public CollisionInfo(boolean hasCollision, Vector3 collisionNormal, Vector3 penetrationDepth) {
        this.hasCollision = hasCollision;
        this.collisionNormal = collisionNormal;
        this.penetrationDepth = penetrationDepth;
    }

    public boolean hasCollision() {
        return hasCollision;
    }

    public Vector3 getCollisionNormal() {
        return collisionNormal;
    }

    public Vector3 getPenetrationDepth() {
        return penetrationDepth;
    }
}