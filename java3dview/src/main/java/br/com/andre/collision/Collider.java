package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

public interface Collider {
    void updatePosition(Vector3 position);
    CollisionInfo checkCollision(Collider other);
    Vector3 getPosition();
}