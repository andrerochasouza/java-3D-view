package br.com.andre.physic;

import br.com.andre.collision.Collider;
import br.com.andre.collision.CollisionInfo;
import br.com.andre.collision.CollisionListener;
import br.com.andre.graphic.Vector3;

import java.util.ArrayList;
import java.util.List;

public class RigidBody implements PhysicsBody {
    private Vector3 position;
    private Vector3 velocity;
    private Vector3 acceleration;
    private double mass;
    private boolean isStatic;
    private Collider collider;
    private List<CollisionListener> listeners;

    public RigidBody(Vector3 position, double mass, Collider collider) {
        this.position = position;
        this.velocity = new Vector3(0, 0, 0);
        this.acceleration = new Vector3(0, 0, 0);
        this.mass = mass;
        this.isStatic = mass == 0;
        this.collider = collider;
        this.listeners = new ArrayList<>();

        if (collider != null) {
            collider.updatePosition(position);
        }
    }

    @Override
    public void update(double deltaTime) {
        if (!isStatic) {
            // Atualiza a velocidade com base na aceleração
            velocity = velocity.add(acceleration.multiply(deltaTime));

            // Atualiza a posição com base na velocidade
            position = position.add(velocity.multiply(deltaTime));

            // Reseta a aceleração para a próxima iteração
            acceleration = new Vector3(0, 0, 0);

            // Atualiza a posição do colisor
            if (collider != null) {
                collider.updatePosition(position);
            }
        }
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public Collider getCollider() {
        return collider;
    }

    @Override
    public void applyForce(Vector3 force) {
        if (!isStatic && mass > 0) {
            acceleration = acceleration.add(force.divide(mass));
        }
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    @Override
    public double getMass() {
        return mass;
    }

    public void addCollisionListener(CollisionListener listener) {
        listeners.add(listener);
    }

    public void notifyCollision(CollisionInfo info, PhysicsBody otherBody) {
        for (CollisionListener listener : listeners) {
            listener.onCollision(info, otherBody);
        }
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3 acceleration) {
        this.acceleration = acceleration;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    public List<CollisionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<CollisionListener> listeners) {
        this.listeners = listeners;
    }
}