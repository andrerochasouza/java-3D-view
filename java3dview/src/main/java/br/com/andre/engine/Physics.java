package br.com.andre.engine;

import br.com.andre.graphic.Vector3;

/**
 * Physics lida com a simulação física básica da câmera.
 */
public class Physics {
    private Vector3 velocity;
    private boolean grounded;
    private static final double GRAVITY = 9.81;

    public Physics() {
        velocity = new Vector3(0, 0, 0);
        grounded = false;
    }

    public void update(double deltaTime) {
        if (!grounded) {
            double newYVelocity = velocity.getY() + GRAVITY * deltaTime;
            velocity = velocity.setY(newYVelocity);
        } else {
            velocity = velocity.setY(0);
        }
    }

    public void applyVerticalForce(double force) {
        velocity = velocity.setY(force);
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }
}