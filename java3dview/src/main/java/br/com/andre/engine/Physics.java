package br.com.andre.engine;

import br.com.andre.graphic.Vector3;

/**
 * A classe Physics lida com a física básica do jogo, como gravidade e forças aplicadas ao jogador.
 */
public class Physics {
    private static final double GRAVITY = 9.81; // Aceleração da gravidade
    private Vector3 velocity; // Velocidade atual do jogador
    private boolean isGrounded; // Indica se o jogador está no chão

    public Physics() {
        velocity = new Vector3(0, 0, 0);
        isGrounded = false;
    }

    /**
     * Atualiza a física do jogador.
     *
     * @param deltaTime O tempo decorrido desde a última atualização (em segundos).
     */
    public void update(double deltaTime) {
        if (!isGrounded) {
            // Aplica gravidade
            velocity = velocity.add(new Vector3(0, GRAVITY * deltaTime, 0));
        }
    }

    /**
     * Aplica uma força vertical ao jogador (pular).
     *
     * @param force A força a ser aplicada (m/s).
     */
    public void applyVerticalForce(double force) {
        velocity = velocity.setY(force);
    }

    /**
     * Obtém a velocidade atual do jogador.
     *
     * @return O vetor de velocidade.
     */
    public Vector3 getVelocity() {
        return velocity;
    }

    /**
     * Define a velocidade atual do jogador.
     *
     * @param velocity O novo vetor de velocidade.
     */
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    /**
     * Verifica se o jogador está no chão.
     *
     * @return true se estiver no chão, false caso contrário.
     */
    public boolean isGrounded() {
        return isGrounded;
    }

    /**
     * Define o estado de estar no chão.
     *
     * @param grounded true se o jogador estiver no chão, false caso contrário.
     */
    public void setGrounded(boolean grounded) {
        this.isGrounded = grounded;
        if (grounded) {
            // Reseta a velocidade vertical ao tocar o chão
            velocity = velocity.setY(0);
        }
    }
}