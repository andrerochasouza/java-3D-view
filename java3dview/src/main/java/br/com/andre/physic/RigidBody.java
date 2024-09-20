package br.com.andre.physic;

import br.com.andre.collision.Collider;
import br.com.andre.collision.collider_object.SphereCollider;
import br.com.andre.graphic.Vector3;

/**
 * Representa um corpo rígido no sistema de física.
 */
public class RigidBody extends PhysicsBody {
    private Vector3 velocity;
    private Vector3 position;
    private double mass;
    private double inverseMass;
    private SphereCollider collider;
    private Vector3 forceAccum;

    public RigidBody(Vector3 startPosition, double mass, SphereCollider collider) {
        this.position = startPosition;
        this.mass = mass;
        this.inverseMass = mass > 0 ? 1.0 / mass : 0.0;
        this.collider = collider;
        this.velocity = new Vector3(0, 0, 0);
        this.forceAccum = new Vector3(0, 0, 0);
    }

    /**
     * Aplica uma força ao corpo.
     *
     * @param force A força a ser aplicada.
     */
    public void applyForce(Vector3 force) {
        forceAccum = forceAccum.add(force);
    }

    @Override
    public void update(double deltaTime) {
        // Calcula a aceleração
        Vector3 acceleration = forceAccum.multiply(inverseMass);

        // Atualiza a velocidade
        velocity = velocity.add(acceleration.multiply(deltaTime));

        // Aplica damping
        double damping = 0.98; // Ajuste conforme necessário
        velocity = velocity.multiply(damping);

        // Atualiza a posição
        position = position.add(velocity.multiply(deltaTime));

        // Atualiza o colisor
        collider.updatePosition(position);

        // Limpa as forças acumuladas
        forceAccum = new Vector3(0, 0, 0);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public Collider getCollider() {
        return collider;
    }

    @Override
    public Vector3 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector3 position) {
        this.position = position;
        collider.updatePosition(position);
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public double getInverseMass() {
        return inverseMass;
    }
}