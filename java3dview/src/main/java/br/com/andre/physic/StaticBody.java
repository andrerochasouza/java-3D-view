package br.com.andre.physic;

import br.com.andre.collision.Collider;
import br.com.andre.graphic.Vector3;

public class StaticBody extends PhysicsBody {
    private Collider collider;

    public StaticBody(Collider collider) {
        this.collider = collider;
    }

    @Override
    public void update(double deltaTime) {
        // Corpos estáticos não precisam ser atualizados
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public Collider getCollider() {
        return collider;
    }

    public void applyForce(Vector3 force) {
        // Corpos estáticos não respondem a forças
    }

    @Override
    public Vector3 getPosition() {
        return collider.getPosition();
    }

    @Override
    public void setPosition(Vector3 position) {

    }

    @Override
    public Vector3 getVelocity() {
        return new Vector3(0, 0, 0);
    }

    @Override
    public void setVelocity(Vector3 velocity) {

    }

    @Override
    public double getMass() {
        return 0;
    }

    @Override
    public double getInverseMass() {
        return 0;
    }
}