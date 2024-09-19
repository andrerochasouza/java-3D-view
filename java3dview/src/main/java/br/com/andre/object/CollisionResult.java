package br.com.andre.object;

import br.com.andre.graphic.Vector3;

public class CollisionResult {
    public boolean collision;
    public Vector3 collisionNormal;

    public CollisionResult(boolean collision, Vector3 collisionNormal) {
        this.collision = collision;
        this.collisionNormal = collisionNormal;
    }
}