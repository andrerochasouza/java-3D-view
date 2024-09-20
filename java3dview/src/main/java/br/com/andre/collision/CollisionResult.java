package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

public class CollisionResult {
    public boolean collision;
    public Vector3 collisionNormal;
    public Vector3 collisionPoint; // Adicionado para obter o ponto de colisão

    public CollisionResult(boolean collision, Vector3 collisionNormal, Vector3 collisionPoint) {
        this.collision = collision;
        this.collisionNormal = collisionNormal;
        this.collisionPoint = collisionPoint;
    }
}