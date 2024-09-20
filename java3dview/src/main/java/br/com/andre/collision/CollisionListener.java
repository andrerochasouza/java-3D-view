package br.com.andre.collision;

import br.com.andre.physic.PhysicsBody;

public interface CollisionListener {
    void onCollision(CollisionInfo collisionInfo, PhysicsBody otherBody);
}