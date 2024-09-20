package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

public class CollisionInfo {
    private boolean collision;
    private Vector3 collisionNormal;
    private Vector3 penetrationDepth;

    public CollisionInfo(boolean collision, Vector3 collisionNormal, Vector3 penetrationDepth) {
        this.collision = collision;
        this.collisionNormal = collisionNormal;
        this.penetrationDepth = penetrationDepth;
    }

    public boolean hasCollision() {
        return collision;
    }

    public Vector3 getCollisionNormal() {
        return collisionNormal;
    }

    public Vector3 getPenetrationDepth() {
        return penetrationDepth;
    }
}