package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

/**
 * Interface que define um colisor para detectar colisões.
 */
public interface Collider {
    /**
     * Atualiza a posição do colisor.
     *
     * @param position A nova posição do colisor.
     */
    void updatePosition(Vector3 position);

    /**
     * Obtém a posição atual do colisor.
     *
     * @return A posição do colisor.
     */
    Vector3 getPosition();

    /**
     * Verifica a colisão com outro colisor.
     *
     * @param other O outro colisor.
     * @return Informações sobre a colisão.
     */
    CollisionInfo checkCollision(Collider other);
}