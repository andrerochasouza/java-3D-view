package br.com.andre.physic;

import br.com.andre.collision.Collider;
import br.com.andre.graphic.Vector3;

public interface PhysicsBody {
    void update(double deltaTime);
    boolean isStatic();
    Collider getCollider();
    void applyForce(Vector3 force);
    Vector3 getPosition();
    Vector3 getVelocity();
    double getMass();
}