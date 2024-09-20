package br.com.andre.engine;

import br.com.andre.graphic.Vector3;

/**
 * Physics lida com a simulação física básica do jogador.
 */
public class Physics {
    private Vector3 velocity;
    private boolean grounded;
    private static final double GRAVITY = 9.81; // Aceleração devido à gravidade (m/s^2)

    public Physics() {
        velocity = new Vector3(0, 0, 0);
        grounded = false;
    }

    /**
     * Atualiza a física do jogador.
     *
     * @param deltaTime O tempo decorrido desde a última atualização (em segundos).
     */
    public void update(double deltaTime) {
        if (!grounded) {
            // Aplica gravidade apenas se o jogador não estiver no chão
            double newYVelocity = velocity.getY() + GRAVITY * deltaTime;
            velocity = velocity.setY(newYVelocity);
        } else {
            // Zera a velocidade vertical se estiver no chão
            velocity = velocity.setY(0);
        }
    }

    /**
     * Aplica uma força vertical ao jogador (por exemplo, salto).
     *
     * @param force A força vertical a ser aplicada (positiva para cima).
     */
    public void applyVerticalForce(double force) {
        velocity = velocity.setY(force);
    }

    /**
     * Redefine a velocidade vertical do jogador.
     */
    public void resetVerticalVelocity() {
        velocity = velocity.setY(0);
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